// Common settings for all projects
lazy val commonSettings = Seq(
  organization := "ch.epfl.gsn",
  version := "2.0.3",
  scalaVersion := "2.12.4",
  javacOptions in (Compile, compile) ++= Seq("-source", "11", "-target", "11"),
  resolvers ++= Seq(
    DefaultMavenRepository,
    "Typesafe Repository" at "https://repo.maven.apache.org/maven2/",
    "osgeo" at "https://repo.osgeo.org/repository/release/",
    "play-authenticate (release)" at "https://oss.sonatype.org/content/repositories/releases/",
    "play-authenticate (snapshot)" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Local ivy Repository" at s"${Path.userHome.asFile.toURI.toURL}/.ivy2/local",
    "Local cache" at s"${file(".").toURI.toURL}lib/cache"
  ),
  publishTo := Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"),

  // PGP settings
  publishMavenStyle := true,
  publishArtifact in (Compile) := false,
  publishArtifact in (Test) := false,
  publishArtifact in (Compile, packageBin) := true,
  publishArtifact in (Compile, packageSrc) := true,
  publishArtifact in (Compile, packageDoc) := false,

  pomIncludeRepository := { _ => false },
  crossPaths := false,
  parallelExecution in Test := false
)

// PGP key hex setting
val usePgpKeyHex = SettingKey[String]("usePgpKeyHex", "PGP key in hex format")

// Root project definition
lazy val root = (project in file(".")).
  aggregate(core, tools, services)

// Core project definition
lazy val core = (project in file("gsn-core")).
  dependsOn(tools).
  settings(commonSettings: _*).
  enablePlugins(JavaServerAppPackaging, DebianPlugin, SystemdPlugin)

// Services project definition
lazy val services = (project in file("gsn-services")).
  dependsOn(tools, core).
  settings(
    coverageEnabled := true,
    coverageExcludedPackages := "<empty>;views.*;router.*;models.gsn.data"
  ).
  settings(commonSettings: _*).
  enablePlugins(PlayJava, PlayEbean, DebianPlugin, SystemdPlugin)

// Tools project definition
lazy val tools = (project in file("gsn-tools")).
  settings(commonSettings: _*)

// WebUI project definition
lazy val webui = (project in file("gsn-webui")).
  enablePlugins(JavaServerAppPackaging, DebianPlugin)

// Custom task to start all GSN modules
lazy val startAll = taskKey[Unit]("Start all the GSN modules")
