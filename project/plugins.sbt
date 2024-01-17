// The Typesafe repository 
resolvers += "Typesafe repository" at "https://repo.maven.apache.org/maven2/"
resolvers += Resolver.typesafeRepo("releases")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.25")

addSbtPlugin("com.github.sbt" % "sbt-eclipse" % "6.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")  


//addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.6.2")  // for sbt-0.13.x or higher

addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "4.1.4")

//addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")


