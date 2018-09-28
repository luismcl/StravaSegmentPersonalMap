name := """StravaSegmentPersonalMap"""
organization := "com.luismiguelcisneros"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

//resolvers += Resolver.file("my-local-repo", file("repo"))

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
libraryDependencies += "com.github.danshannon" % "javastrava-api" % "1.0.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.8.2"
libraryDependencies += "com.github.kiambogo" %% "scrava" % "1.3.0"

libraryDependencies += "io.netty" % "netty-all" % "4.1.13.Final"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "net.liftweb" %% "lift-json" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "joda-time" % "joda-time" % "2.9.7",
  "org.joda" % "joda-convert" % "1.8"
)



// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.luismiguelcisneros.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.luismiguelcisneros.binders._"
