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

//include multiformatsample.xml and start core if not executable
class ServicesTest extends PlaySpec {

  val actorSystem = ActorSystem("test")
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val app = new GuiceApplicationBuilder()
    .bindings(
      bind[ControllerComponents].to[DefaultControllerComponents]
    )
    .build()

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


    //sensors
    "return sensor data in JSON format" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors?format=json")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }

    "return sensor data in CSV format" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors?format=csv")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/plain")
    }

    "return sensor data in XML format" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors?format=xml")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/xml")
    }

    "return sensor data in default response format (JSON)" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val request = FakeRequest("GET", "/api/sensors")
      val result = sensorService.sensors()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }


    //userinfo
    "return user info in JSON format" in {
      val oauthcontroller = app.injector.instanceOf[OAuth2Controller]
      val oauth2request = FakeRequest("POST", "/oauth2/token")
      val requestData = Map(
        "grant_type" -> "client_credentials",
        "client_id" -> "web-gui-public",
        "client_secret" -> "web-gui-secret"
      )

      val futureAccessToken = oauthcontroller.accessToken()(oauth2request.withFormUrlEncodedBody(requestData.toSeq: _*))

      val tokenresult: Result = Await.result(futureAccessToken, 10.seconds)

      // Check if the response status is 200 OK
      if (tokenresult.header.status == OK) {
        // Parse the JSON content to extract the access token
        val json = Json.parse(contentAsString(futureAccessToken))
        val access_token = (json \ "access_token").as[String]
        val request = FakeRequest("GET", "/api/user").withHeaders(
          "Authorization" -> s"Bearer $access_token"
        )
        val sensorService = app.injector.instanceOf[SensorService]
        val result = sensorService.userInfo()(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
      } else {
        println(s"Access token request failed with status: ${tokenresult.header.status}")
      }
    }




/*    "return Forbidden for unauthenticated request" in {
      // Create a FakeRequest without an OAuth token
      val request = FakeRequest("GET", "/api/user")
      val sensorService = app.injector.instanceOf[SensorService]
      val result = sensorService.userInfo()(request)

      status(result) mustBe FORBIDDEN
    }*/



  }




}
