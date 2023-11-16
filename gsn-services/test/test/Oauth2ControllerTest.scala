package test

import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import controllers.gsn.api.DataProcessService
import controllers.gsn.api.SensorService
import controllers.gsn.OAuth2Controller
import controllers.gsn.GSNDataHandler
import controllers.gsn.auth.PermissionsController
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json._
import org.scalatest.BeforeAndAfterAll
import play.api.test.CSRFTokenHelper._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import scala.concurrent.Future
import play.api.db.Database
import java.net.URL
import scala.util.Try
import scala.util.{Success, Failure}

class Oauth2ControllerTest extends PlaySpec with BeforeAndAfterAll{

  val actorSystem = ActorSystem("test")
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val app = new GuiceApplicationBuilder()
    .bindings(
      bind[ControllerComponents].to[DefaultControllerComponents]
    )
    .build()

  
  var db: Database = _
  var access_token: String = ""
  var refresh_token: String = ""

  override def beforeAll(): Unit = {
    db = app.injector.instanceOf[Database];

    play.api.Play.start(app)
  }
  override def afterAll(): Unit = {
    play.api.Play.stop(app)
  }

    "Oauth2Controller" should {
        "handle accessToken " in {
                var authController = app.injector.instanceOf[OAuth2Controller]
                val oauth2request = FakeRequest("POST", "/oauth2/token")
                val requestData = Map(
                    "grant_type" -> "password",
                    "username" -> "root@localhost",
                    "password" -> "changeme",
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-secret"
                )

                val futureAccessToken = authController.accessToken()(oauth2request.withFormUrlEncodedBody(requestData.toSeq: _*))

                val tokenresult: Result = await(futureAccessToken)
                if (tokenresult.header.status == OK) {
                    val json = Json.parse(contentAsString(futureAccessToken))
                    access_token = (json \ "access_token").as[String]
                    refresh_token = (json \ "refresh_token").as[String]

                    val refreshRequestData = Map(
                        "grant_type" -> "refresh_token",
                        "refresh_token" -> refresh_token,
                        "client_id" -> "web-gui-public",
                        "client_secret" -> "web-gui-secret"
                    )
                    val futureRefreshToken = authController.accessToken()(oauth2request.withFormUrlEncodedBody(refreshRequestData.toSeq: _*))
                    val refreshResult: Result = await(futureRefreshToken)
                    status(futureAccessToken) mustBe OK
                    contentType(futureAccessToken) mustBe Some("application/json")
                    } else {
                    throw new RuntimeException(s"Access token request failed with status: ${tokenresult.header.status}")
                    }
                status(futureAccessToken) mustBe OK
                contentType(futureAccessToken) mustBe Some("application/json")
        }
        "handle auth" in{
            var authController = app.injector.instanceOf[OAuth2Controller]
            var request = FakeRequest("GET", s"/oauth2/auth")
                    .withHeaders("Authorization" -> s"Bearer $access_token")
            var result = authController.auth()(request)
            status(result) mustBe SEE_OTHER

            var redirectLocation = header("Location", result)
            redirectLocation mustBe Some("/login")

            val queryString = "?response_type=code&client_id=web-gui-public&client_secret=web-gui-secret"
            request = FakeRequest("GET", s"/oauth2/auth$queryString")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            var futureResult = authController.auth()(request)
            var result1 = await(futureResult)
            status(futureResult) mustBe BAD_REQUEST

            val queryString1 = "?response_type=no&client_id=web-gui-public&client_secret=web-gui-secret"
            request = FakeRequest("GET", s"/oauth2/auth$queryString1")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            futureResult = authController.auth()(request)
            result1 = await(futureResult)
            status(futureResult) mustBe 501 //not implemented
        }

        "handle doAuth" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
            var datahandler = app.injector.instanceOf[GSNDataHandler]
                            //handle POST request to doAuth
            var request = FakeRequest("POST", s"/oauth2/auth")
                    .withHeaders("Authorization" -> s"Bearer $access_token")

            var futureResult = authController.doAuth()(request)
            val result2: Result = await(futureResult)
            status(futureResult) mustBe SEE_OTHER
            var redirectLocation = header("Location", futureResult)
            redirectLocation mustBe Some("/login")

            request = FakeRequest("POST", "/oauth2/auth")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")

            futureResult = authController.doAuth()(request)
            val result3: Result = await(futureResult)
            status(futureResult) mustBe BAD_REQUEST

            val requestdoAuth = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "response_type" -> "code",  
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-secret",
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
            val futureResultdoAuth: Future[Result] = authController.doAuth()(requestdoAuth)
            val resultdoAuth: Result = await(futureResultdoAuth)
            status(futureResultdoAuth) mustBe 303 //redirect 

            val locationOption: Option[String] = resultdoAuth.header.headers.get("Location").map(_.mkString)

            locationOption.foreach { location =>
            val codeOption = Try {
                val url = new URL(location)
                val queryParams = url.getQuery.split("&")
                val codeParam = queryParams.find(_.startsWith("code=")).getOrElse("")
                codeParam.substring("code=".length)
            }.toOption

            codeOption.foreach { code =>
                val authinfo= datahandler.findAuthInfoByCode(code)
                authinfo.onComplete {
                    case Success(result) =>
                        result mustBe a[Some[_]] 
                    case Failure(exception) =>
                        fail(s"Failed to retrieve AuthInfo: $exception")
                }
                val deleteresp= datahandler.deleteAuthCode(code)
                val finaldeleteresp= await(deleteresp)
                deleteresp.map { result =>
                    result mustBe a[Success[_]] 
                }
            }
            }
                
            val requestdoAuthforbidden = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "response_type" -> "code",  
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-public",
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
            val futureResultdoAuthforbidden: Future[Result] = authController.doAuth()(requestdoAuthforbidden)
            val resultdoAuthforbidden: Result = await(futureResultdoAuthforbidden)
            status(futureResultdoAuthforbidden) mustBe FORBIDDEN
        }

        "list clients" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                //list clients
                val requestclient = FakeRequest("GET", "/oauth2/client")
                            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                            .withCSRFToken
                val futureResultclient = authController.listClients()(requestclient)
                val resultclient = await(futureResultclient)
                status(futureResultclient) mustBe OK
        }


        "add a client" in {
            //add client
            var authController = app.injector.instanceOf[OAuth2Controller]
                val requestadd = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "0",
                    "action" -> "add",
                    "name" -> "testclient",
                    "client_id" -> "testclient_id",
                    "client_secret" -> "testclient_secret",
                    "redirect"->"http://xy.z",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                val futureResultadd : Future[Result] = authController.editClient(requestadd)
                status(futureResultadd) mustBe OK
        }


        "edit an existing client" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                //edit existing client
                val requestedit = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "1",
                    "action" -> "edit",
                    "name" -> "Default Web UI",
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-secret",
                    "redirect"->"http://localhost:8000/profile/",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                val futureResultedit : Future[Result] = authController.editClient(requestedit)
                status(futureResultedit) mustBe OK
        }

            "edit non existing client" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                //edit existing client
                val requestedit = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "99",
                    "action" -> "edit",
                    "name" -> "Default Web UI",
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-secret",
                    "redirect"->"http://localhost:8000/profile/",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                val futureResultedit : Future[Result] = authController.editClient(requestedit)
                status(futureResultedit) mustBe 404 //not found
        }


        "delete a client" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                val requestdel = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "99",
                    "action" -> "del",
                    "name" -> "testclient",
                    "client_id" -> "testclient_id",
                    "client_secret" -> "testclient_secret",
                    "redirect"->"http://xy.z",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                var futureResultdel: Future[Result] = authController.editClient(requestdel)
                status(futureResultdel) mustBe 404 //not found

                //BAD REQUEST
                val badrequest = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "99",
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                val futureResultbad: Future[Result] = authController.editClient(badrequest)
                status(futureResultbad) mustBe BAD_REQUEST

                
                val requestsuccesfuldel = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "1",
                    "action" -> "del",
                    "name" -> "testclient",
                    "client_id" -> "testclient_id",
                    "client_secret" -> "testclient_secret",
                    "redirect"->"http://xy.z",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                futureResultdel = authController.editClient(requestsuccesfuldel)
                status(futureResultdel) mustBe OK

        }      

    }
}
