name := "pepper-stone"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

mainClass in (Compile, run) := Some("myPackage.aMainClass")

mainClass in (Compile, packageBin) := Some("myPackage.anotherMainClass")