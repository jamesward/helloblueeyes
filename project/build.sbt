name := "HelloBlueEyes"
 
version := "0.1"
 
organization  := "net.interdoodle"

scalaVersion := "2.9.1"

resolvers += Classpaths.typesafeResolver

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.7" % "test->default"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.3.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.4.0")
