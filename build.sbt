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
  "com.nimbusds" % "nimbus-jose-jwt" % "4.0",
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "org.slf4j" % "slf4j-nop" % "1.7.12",
  "org.flywaydb" %% "flyway-play" % "2.2.0"
)

routesGenerator := InjectedRoutesGenerator

fork in run := false