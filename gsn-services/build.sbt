name := "gsn-services"

import NativePackagerHelper._
import com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader
import com.typesafe.sbt.packager.archetypes.TemplateWriter
import com.typesafe.sbt.packager.linux._
import spray.revolver.RevolverPlugin._

Revolver.settings

val buildSettings = Seq(
   javaOptions += "-Xmx512m",
   javaOptions += "-Xms64m"
)

Compile / sources := Seq.empty
doc / sources := Seq.empty

libraryDependencies ++= Seq(
  "be.objectify"  %% "deadbolt-java"     % "2.6.4",
  "be.objectify"  %% "deadbolt-scala"    % "2.6.0",
  "be.objectify"  %% "deadbolt-java-gs"  % "2.6.0",
  // Comment the next line for local development of the Play Authentication core:
  // Use the latest release version when copying this code, e.g. "0.9.0"
  "com.feth"      %% "play-authenticate" % "0.9.0",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "com.h2database" % "h2" % "1.4.195",
//  "com.h2database" % "h2" % "2.2.220",
  cacheApi,
  ehcache,
  "com.google.inject" % "guice" % "4.2.1",
  evolutions,
  javaWs,
  javaJdbc,
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.easytesting" % "fest-assert" % "1.4" % "test",
  "org.fluentlenium" % "fluentlenium-core" % "3.6.1" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "3.14.0" % "test",
  "org.seleniumhq.selenium" % "selenium-api" % "3.14.0" % "test",
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "3.14.0" % "test",
  "org.seleniumhq.selenium" % "selenium-support" % "3.14.0" % "test",
  "javax.xml.bind" % "jaxb-api" % "2.3.1",
  "org.glassfish.jaxb" % "jaxb-runtime" % "2.3.1",
  "com.esotericsoftware.kryo" % "kryo" % "2.23.0",
  "com.nulab-inc" %% "scala-oauth2-core" % "1.5.0",
  "com.nulab-inc" %% "play2-oauth2-provider" % "1.5.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59" % "test"
  //"ch.epfl.gsn" % "gsn-core" % "2.0.3" exclude("org.apache.logging.log4j", "log4j-slf4j-impl") exclude("org.scala-lang.modules", "scala-xml_2.11")
)

libraryDependencies := libraryDependencies.value.map(_.exclude("ch.qos.logback", "logback-classic").exclude("ch.qos.logback", "logback-core"))

//excludeDependencies ++= Seq(
//  SbtExclusionRule("org.apache.logging.log4j", "log4j-slf4j-impl"),
//  SbtExclusionRule("org.hibernate", "hibernate-core")
//)

//NativePackagerKeys.packageSummary in com.typesafe.sbt.SbtNativePackager.Linux := "GSN Services"
//NativePackagerKeys.packageDescription := "Global Sensor Networks Services"
//NativePackagerKeys.maintainer in com.typesafe.sbt.SbtNativePackager.Linux := "LSIR EPFL <gsn@epfl.ch>"
//debianPackageDependencies in Debian += "java11-runtime"
//debianPackageRecommends in Debian ++= Seq("postgresql", "gsn-core", "nginx")
//serverLoading in Debian := Some(ServerLoader.Systemd)
//daemonUser in Linux := "gsn"

enablePlugins(DebianPlugin)
enablePlugins(SystemdPlugin)

name := "gsn-services"

com.typesafe.sbt.SbtNativePackager.Linux / NativePackagerKeys.packageSummary := "GSN Services"
com.typesafe.sbt.SbtNativePackager.Linux / NativePackagerKeys.packageDescription := "Global Sensor Networks Services"
com.typesafe.sbt.SbtNativePackager.Linux / NativePackagerKeys.maintainer := "LSIR EPFL <gsn@epfl.ch>"

Debian / debianPackageDependencies += "java11-runtime"
Debian / debianPackageRecommends ++= Seq("mysql", "postgresql", "gsn-core", "nginx")

Linux / daemonUser := "views/gsn"

Test / javaOptions += "-Dconfig.file=conf/test.conf"
