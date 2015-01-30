import AssemblyKeys._  // put this at the top of the file

organization := "ru.biocad"

name := "pepper-stone"

version := "1.0"

description := "Clinical Data Merge System"

scalaVersion := "2.11.2"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

resolvers += Resolver.sonatypeRepo("public")

mainClass in (Compile, run) := Some("ru.biocad")

mainClass in (Compile, packageBin) := Some("ru.biocad")

mainClass in assembly := Some("ru.biocad")

assemblySettings