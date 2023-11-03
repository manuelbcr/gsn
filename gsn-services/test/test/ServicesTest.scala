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
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json._
import org.scalatest.BeforeAndAfter

//include multiformatsample.xml and start core if not executable
//important also add the user to virtual sensor in frontend in order to access the data 
class ServicesTest extends PlaySpec with BeforeAndAfter{

  val actorSystem = ActorSystem("test")
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val app = new GuiceApplicationBuilder()
    .bindings(
      bind[ControllerComponents].to[DefaultControllerComponents]
    )
    .build()



  var access_token: String = ""

  before {
    // Run once before all tests to obtain the access token
    val oauthcontroller = app.injector.instanceOf[OAuth2Controller]
    val oauth2request = FakeRequest("POST", "/oauth2/token")
    val requestData = Map(
      "grant_type" -> "client_credentials",
      "client_id" -> "web-gui-public",
      "client_secret" -> "web-gui-secret"
    )

    val futureAccessToken = oauthcontroller.accessToken()(oauth2request.withFormUrlEncodedBody(requestData.toSeq: _*))

    val tokenresult: Result = await(futureAccessToken)
    println(tokenresult)

    if (tokenresult.header.status == OK) {
      val json = Json.parse(contentAsString(futureAccessToken))
      access_token = (json \ "access_token").as[String]
    } else {
      throw new RuntimeException(s"Access token request failed with status: ${tokenresult.header.status}")
    }
  }

  "DataProcessService" should {
    "processData method should return JSON response" in {
      val controller = app.injector.instanceOf[DataProcessService]
      val request = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10&format=json")
      val result = controller.processData("MultiFormatTemperatureHandler", "light")(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }

    "return CSV response for processData method" in {
      val controller = app.injector.instanceOf[DataProcessService]
      val request = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10&format=csv")
      val result = controller.processData("MultiFormatTemperatureHandler", "light")(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/plain")
    }

    "return XML response for processData method" in {
      val controller = app.injector.instanceOf[DataProcessService]
      val request = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10&format=xml")
      val result = controller.processData("MultiFormatTemperatureHandler", "light")(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/plain")
    }

    "processData method should use the default response format (JSON)" in {
        val controller = app.injector.instanceOf[DataProcessService]
        val request = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10")
        val result = controller.processData("MultiFormatTemperatureHandler", "light")(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
    }

    "processData method should return BadRequest for wrong sensor" in {
    val controller = app.injector.instanceOf[DataProcessService]
    val request = FakeRequest("GET", "/api/data?sensorid=wrong_sensor_id&fieldid=1&op=wma&params=10&format=json")

    val result = controller.processData("wrong_sensor_id", "wrong_field_id")(request)

    status(result) mustBe BAD_REQUEST
    contentAsString(result) must include("Sensor id wrong_sensor_id is not valid.")
    }

  }



  "SensorService" should {

    //##################################################### sensors #####################################################
    "return sensors in JSON format" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors?format=json")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }

    "return sensors in CSV format" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors?format=csv")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/plain")
    }

    "return sensors in XML format" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors?format=xml")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/xml")
    }

    "return sensors in default response format (JSON)" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }


    //##################################################### userinfo #####################################################
    "return user info in JSON format for userinfo request" in {
        val request = FakeRequest("GET", "/api/user").withHeaders(
          "Authorization" -> s"Bearer $access_token"
        )
        val sensorService = app.injector.instanceOf[SensorService]
        val result = sensorService.userInfo()(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")

    }

    "return BAD_REQUEST for userinfo request with no token" in {
      val request = FakeRequest("GET", "/api/user")
      val sensorService = app.injector.instanceOf[SensorService]
      val result = sensorService.userInfo()(request)
      status(result) mustBe BAD_REQUEST
    }

    "return unauthorized for userinfo request with wrong token" in {
      val request = FakeRequest("GET", "/api/user").withHeaders(
          "Authorization" -> s"Bearer fake_token"
      )
      val sensorService = app.injector.instanceOf[SensorService]
      val result = sensorService.userInfo()(request)
      status(result) mustBe UNAUTHORIZED
    }

    //##################################################### sensorData #####################################################
      "return sensor data in JSON format with expectedFields and fieldTypes" in {
        val params = Map("size" -> "10", "fields" -> "light,temperature,packet_type","filter"-> "light > 100,temperature <100")

        val request = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler/data?${params.map { case (key, value) => s"$key=$value" }.mkString("&")}")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        println(request)
        val sensorService = app.injector.instanceOf[SensorService]
        val futureResult = sensorService.sensorData("MultiFormatTemperatureHandler")(request)
        val result: Result = await(futureResult)

        status(futureResult) mustBe OK
        contentType(futureResult) mustBe Some("application/json")
   
        val jsonResult = Json.parse(contentAsString(futureResult))
        println(jsonResult)

        (jsonResult \ "type").as[String] mustBe "Feature"

        val properties = (jsonResult \ "properties").as[JsObject]
        (properties \ "vs_name").as[String] mustBe "MultiFormatTemperatureHandler"

        val fields = (properties \ "fields").as[Seq[JsObject]]

            // Define the expected fields and their types
            val expectedFields = Map(
              "light" -> "double",
              "temperature" -> "double",
              "packet_type" -> "double"
            )

            for ((fieldName, fieldType) <- expectedFields) {
              val field = fields.find(field => (field \ "name").as[String] == fieldName)
              field.isDefined mustBe true
              val fieldJsType = (field.get \ "type").as[String]
              fieldJsType mustBe fieldType
            }
     
        
      }
  }




}
