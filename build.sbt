import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name := "helloblueeyes"

version := "1.0"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
  "Sonatype"    at "http://nexus.scala-tools.org/content/repositories/public",
  "Scala Tools" at "http://scala-tools.org/repo-releases/",
  "JBoss"       at "http://repository.jboss.org/nexus/content/groups/public/",
  "GuiceyFruit" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
)

libraryDependencies ++= Seq(
  "se.scalablesolutions.akka" % "akka-actor"       % "latest.integration"              withSources(),
  "se.scalablesolutions.akka" % "akka-stm"         % "latest.integration"              withSources(),
  "se.scalablesolutions.akka" % "akka-typed-actor" % "latest.integration"              withSources(),
  "com.reportgrid"           %% "blueeyes"         % "latest.integration"  % "compile" withSources(),
  "org.scalatest"            %% "scalatest"        % "latest.integration"  % "test"    withSources()
)

