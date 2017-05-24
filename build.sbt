name := """kobold"""

version := "1.0-android-testing"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212"
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "5.0.0"

dockerUpdateLatest := true
