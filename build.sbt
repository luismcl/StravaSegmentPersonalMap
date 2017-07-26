name := """StravaSegmentPersonalMap"""
organization := "com.luismiguelcisneros"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
libraryDependencies += "com.github.danshannon" % "javastrava-api" % "1.0.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.8.2"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0"
libraryDependencies += "io.netty" % "netty-all" % "4.1.13.Final"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.luismiguelcisneros.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.luismiguelcisneros.binders._"
