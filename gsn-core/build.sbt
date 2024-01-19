name := "gsn-core"

Revolver.settings

libraryDependencies ++= Seq(
  //"com.typesafe" % "config" % "1.2.1",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "com.h2database" % "h2" % "1.4.195",
  "com.typesafe.play" %% "play" % "2.6.0",
  //"mysql" % "mysql-connector-java" % "5.1.29",
  "mysql" % "mysql-connector-java" % "8.0.28",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "org.apache.commons" % "commons-dbcp2" % "2.0",
  "org.hibernate" % "hibernate-core" % "3.6.10.Final",
  "org.apache.httpcomponents" % "httpclient" % "4.3.2",
  "org.apache.commons" % "commons-email" % "1.3.2",
  "commons-collections" % "commons-collections" % "3.2.1",
  "commons-io" % "commons-io" % "2.4",
  "org.apache.logging.log4j" % "log4j-api" % "2.3",
  "org.apache.logging.log4j" % "log4j-core" % "2.3",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.3",
  "org.apache.logging.log4j" % "log4j-web" % "2.3",
  "net.sf.opencsv" % "opencsv" % "2.3",
  "com.thoughtworks.xstream" % "xstream" % "1.4.5",
  "org.antlr" % "stringtemplate" % "3.0",
  "commons-lang" % "commons-lang" % "2.6",
  "rome" % "rome" % "1.0",
  "org.jfree" % "jfreechart" % "1.0.19", 
  "org.jfree" % "jcommon" % "1.0.23",
  "org.codehaus.groovy" % "groovy-all" % "2.2.2",
  "net.rforge" % "REngine" % "0.6-8.1",
  "net.rforge" % "Rserve" % "0.6-8.1",
  "org.rxtx" % "rxtx" % "2.1.7",
  "com.esotericsoftware.kryo" % "kryo" % "2.23.0",
  "org.zeromq" % "jeromq" % "0.3.5",
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.1.0",
  "org.eclipse.californium" % "californium-core" % "1.0.4",
  "junit" % "junit" % "4.11" %  "test",
  //"ch.epfl.gsn" % "gsn-tools" % "2.0.3",
  "org.easymock" % "easymockclassextension" % "3.2" % "test",
  "commons-fileupload" % "commons-fileupload" % "1.3.3",
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
  "com.jfinal" % "cos" % "2022.2",
  "org.eclipse.jetty" % "jetty-continuation" % "9.4.43.v20210629",
  "org.eclipse.jetty" % "jetty-io" % "9.4.43.v20210629",
  "org.jibx" % "jibx-run" % "1.3.1",
  "org.httpunit" % "httpunit" % "1.7.2" % "test" exclude("xerces","xercesImpl") exclude("xerces","xmlParserAPIs") exclude("javax.servlet","servlet-api")
)

mainClass in Compile := Some("ch.epfl.gsn.Main")

enablePlugins(JavaServerAppPackaging)

// Define package summary, description, and maintainer
NativePackagerKeys.packageSummary in Linux := "GSN Server"
NativePackagerKeys.packageSummary in Windows := "GSN Server"
NativePackagerKeys.packageDescription := "Global Sensor Networks Core"
NativePackagerKeys.maintainer in Linux := "LSIR EPFL <gsn@epfl.ch>"
NativePackagerKeys.maintainer in Windows := "LSIR EPFL <gsn@epfl.ch>"

//mappings for config file in Universal package
mappings in Universal ++= Seq(
  (sourceDirectory.value / "templates" / "gsn-core") -> "bin/gsn-core",
  (sourceDirectory.value / "main" / "resources" / "log4j2.xml") -> "conf/log4j2.xml",
  (baseDirectory.value / ".." / "conf" / "gsn.xml") -> "conf/gsn.xml",
  (sourceDirectory.value / "main" / "resources" / "wrappers.properties") -> "conf/wrappers.properties"
)

// mappings for virtual-sensors in Universal package
mappings in Universal ++= {
  val sampleFiles = (baseDirectory.value / ".." / "virtual-sensors" / "samples" * "").get
  sampleFiles.map(file => file -> ("virtual-sensors-samples/" + file.getName))
}

// Define daemon user for Linux
daemonUser in Linux := "gsn"

// Define Debian package dependencies
debianPackageDependencies in Debian += "java11-runtime"
debianPackageRecommends in Debian ++= Seq("postgresql", "munin-node", "gsn-services")

// mappings for config files in Debian package including access permissions
linuxPackageMappings := {
  val mappings = linuxPackageMappings.value
  mappings map { 
    case linuxPackage if linuxPackage.fileData.config equals "true" =>
      val newFileData = linuxPackage.fileData.copy(
        user = "gsn"
      )
      linuxPackage.copy(
        fileData = newFileData
      )
    case linuxPackage => linuxPackage
  }
}

// mappings for virtual sensors in Debian package including access permissions
linuxPackageMappings in Debian += packageMapping(
  (baseDirectory.value / ".." / "virtual-sensors" / "packaged") -> "/usr/share/gsn-core/conf/virtual-sensors"
) withUser "gsn" withGroup "root" withPerms "0775" withContents()

// Set reStart arguments using sbt-revolver
mainClass in reStart := Some("ch.epfl.gsn.Main")
reStartArgs := Seq("../conf", "../virtual-sensors")

