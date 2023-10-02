//import com.typesafe.sbt.packager.archetypes.ServerLoader

name := "gsn-services"


val buildSettings = Seq(
   javaOptions += "-Xmx128m",
   javaOptions += "-Xms64m"
)

sources in (Compile,doc) := Seq.empty

libraryDependencies ++= Seq(
  jdbc,
  //ws,
  //cache,
  //javaEbean,
  "com.typesafe.play" %% "play-ws" % "2.6.0",
  "com.typesafe.play" %% "play-functional" % "2.6.0",
  "com.h2database" % "h2" % "1.4.181",
  "mysql" % "mysql-connector-java" % "8.0.28",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "be.objectify" %% "deadbolt-java" % "2.6.0",
  "be.objectify"  %% "deadbolt-scala"     % "2.6.0",
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0",
  "com.nulab-inc" %% "play2-oauth2-provider" % "1.3.0",
  "com.feth" %% "play-authenticate" % "0.9.0",
  "com.google.inject" % "guice" % "4.1.0",
  javaCore,
  "com.esotericsoftware.kryo" % "kryo" % "2.23.0",
  "org.zeromq" % "jeromq" % "0.3.5",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.4",
  //"ch.epfl.gsn" % "gsn-core" % "2.0.1" exclude("org.apache.logging.log4j", "log4j-slf4j-impl"),
  "com.typesafe.play" %% "play-json" % "2.6.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.3"
  )

//libraryDependencies := libraryDependencies.value.map(_.exclude("ch.qos.logback", "logback-classic").exclude("ch.qos.logback", "logback-core"))


NativePackagerKeys.packageSummary in com.typesafe.sbt.SbtNativePackager.Linux := "GSN Services"

NativePackagerKeys.packageDescription := "Global Sensor Networks Services"

NativePackagerKeys.maintainer in com.typesafe.sbt.SbtNativePackager.Linux := "LSIR EPFL <gsn@epfl.ch>"

debianPackageDependencies in Debian += "java11-runtime"

debianPackageRecommends in Debian ++= Seq("postgresql", "gsn-core", "nginx")

//serverLoading in Debian := ServerLoader.Systemd

enablePlugins(DebianPlugin)

enablePlugins(SystemdPlugin)

daemonUser in Linux := "gsn"
