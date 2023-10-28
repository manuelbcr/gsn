package models.gsn.data

import com.typesafe.config.ConfigFactory
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import scala.concurrent.Await
import concurrent.duration._

class GsnMetadataTest extends PlaySpec  {
  
  implicit lazy val app: Application = new GuiceApplicationBuilder().build() 
/**
  "gsn metadata" must{
    lazy val conf=ConfigFactory.load
    lazy val gsnServer=conf.getString("gsn.server.url")
    import concurrent.ExecutionContext.Implicits.global
    val gsn=new GsnMetadata("http://montblanc.slf.ch:22001")
    "get all sensors" in{
      println("pipocas")
      val t=Await.result(gsn.getGsnSensors,30 seconds)
      println("pipocas2 "+t.size)

    }
  }
**/

}