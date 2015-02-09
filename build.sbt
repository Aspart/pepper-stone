import AssemblyKeys._

organization := "ru.biocad"

name := "pepper-stone"

version := "1.0"

description := "Clinical Data Merge System"

scalaVersion := "2.11.4"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "org.slf4j" % "slf4j-simple" % "1.6.4"
)

resolvers += Resolver.sonatypeRepo("public")

mainClass in (Compile, run) := Some("ru.biocad")

mainClass in (Compile, packageBin) := Some("ru.biocad")

mainClass in assembly := Some("ru.biocad")

assemblySettings