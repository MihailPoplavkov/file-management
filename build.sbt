name := "file-management"
organization := "com.poplavkov"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  guice,
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.405",
  "io.swagger" %% "swagger-play2" % "1.6.0", // swagger api
  "org.webjars" % "swagger-ui" % "3.18.2", // swagger ui
  "org.webjars" %% "webjars-play" % "2.6.3",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.scalamock" %% "scalamock" % "4.1.0" % Test
)
