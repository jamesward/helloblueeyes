import com.typesafe.startscript.StartScriptPlugin

// add all the settings in (StartScriptPlugin.startScriptForClassesSettings into this project
seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name := "helloblueeyes"

version := "1.0"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
  "Sonatype"          at "http://nexus.scala-tools.org/content/repositories/public",
  "Scala Tools"       at "http://scala-tools.org/repo-releases/",
  "JBoss"             at "http://repository.jboss.org/nexus/content/groups/public/",
  "GuiceyFruit"       at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
)

libraryDependencies ++= Seq(
  "com.reportgrid" %% "blueeyes" % "0.4.24" withSources()
)