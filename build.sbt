name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212"
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "5.0.0"
