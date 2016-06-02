name := "scalaserver"

version := "1.0"

scalaVersion := "2.11.8"

// http://mvnrepository.com/artifact/javax.servlet/javax.servlet-api
libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1"

TaskKey[Unit]("sayhello") := println("hello world!")
