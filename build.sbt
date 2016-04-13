import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayScala

name := """luxury-akka"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.postgresql" % "postgresql" % "9.4-1202-jdbc41",
  "com.nimbusds" % "nimbus-jose-jwt" % "4.11.2",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.flywaydb" %% "flyway-play" % "2.2.1",
  "org.apache.commons" % "commons-email" % "1.4",
  "com.github.tminglei" %% "slick-pg" % "0.12.0",
  "com.github.kenglxn.QRGen" % "javase" % "2.2.0"
)

routesGenerator := InjectedRoutesGenerator

fork in run := false