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

    "return XML response for processData method with linear-interp" in {
      val controller = app.injector.instanceOf[DataProcessService]
      val request = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=linear-interp&params=10&format=xml")
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

        (jsonResult \ "type").as[String] mustBe "Feature"

        val properties = (jsonResult \ "properties").as[JsObject]
        (properties \ "vs_name").as[String] mustBe "MultiFormatTemperatureHandler"

        val fields = (properties \ "fields").as[Seq[JsObject]]
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
      //##################################################### sensorField #####################################################
      "return sensor field data" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val sensorId = "MultiFormatTemperatureHandler"
        val fieldId = "light"
        val request = FakeRequest("GET", s"/api/sensors/$sensorId/fields/$fieldId")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val result = sensorService.sensorField(sensorId, fieldId)(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("text/plain")

      }

      //##################################################### sensorMetadata #####################################################
      "return sensor metadata in the specified format (JSON)" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val sensorId = "MultiFormatTemperatureHandler"
        val latestValues = "true" 
        val format = "json" 
        val request = FakeRequest("GET", s"/api/sensors/$sensorId/metadata?latestValues=$latestValues&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val result = sensorService.sensorMetadata(sensorId)(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")

      }

      "return sensor metadata in the specified format (CSV)" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val sensorId = "MultiFormatTemperatureHandler"
        val latestValues = "true" 
        val format = "csv" 
        val request = FakeRequest("GET", s"/api/sensors/$sensorId/metadata?latestValues=$latestValues&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val result = sensorService.sensorMetadata(sensorId)(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("text/plain")

      }

      "return sensor metadata in the specified format (xml)" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val sensorId = "MultiFormatTemperatureHandler"
        val latestValues = "true" 
        val format = "xml" 
        val request = FakeRequest("GET", s"/api/sensors/$sensorId/metadata?latestValues=$latestValues&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val result = sensorService.sensorMetadata(sensorId)(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("text/plain")

      }


      //##################################################### sensorSearch #####################################################
      "return sensor search results in the specified format" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val vsnames = "MultiFormatTemperatureHandler" 
        val size = 10 
        val fields = "light,temperature" 
        val format = "json" 
        val request = FakeRequest("GET", s"/api/sensors/search?vsnames=$vsnames&size=$size&fields=$fields&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val result = sensorService.sensorSearch(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
       
        val formatcsv = "csv" 
        val requestcsv = FakeRequest("GET", s"/api/sensors/search?vsnames=$vsnames&size=$size&fields=$fields&format=$formatcsv")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val resultcsv = sensorService.sensorSearch(requestcsv)

        status(resultcsv) mustBe OK
        contentType(resultcsv) mustBe Some("application/zip")

        val formatxml = "xml" 
        val requestxml = FakeRequest("GET", s"/api/sensors/search?vsnames=$vsnames&size=$size&fields=$fields&format=$formatxml")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val resultxml = sensorService.sensorSearch(request)

        status(resultxml) mustBe OK
        contentType(resultxml) mustBe Some("application/json")
      }

      //##################################################### uploadSensorData #####################################################
     
     /*
      "upload sensor data and forward it to GSN core" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val sensorId = "MultiFormatTemperatureHandler"
        val jsonData = Json.obj(
          "light" -> 400,
          "temperature" -> 42
        ) 

        val request = FakeRequest("POST", s"/api/sensors/$sensorId/data")
          .withHeaders("Authorization" -> s"Bearer $access_token")
          .withJsonBody(jsonData)
        val futureResult= sensorService.uploadSensorData(sensorId)(request)
        val result = await(futureResult) 
        println(result)
        //status(futureResult) mustBe OK
        //contentType(futureResult) mustBe Some("application/json")
        //contentAsString(futureResult) must include("success")
      }
      */
  }




}
