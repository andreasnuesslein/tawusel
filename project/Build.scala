import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "TaxiMob"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "mysql" % "mysql-connector-java" % "5.1.18",
      "se.scalablesolutions.akka" % "akka-actor" % "1.1.3",
      "se.scalablesolutions.akka" % "akka-typed-actor" % "1.1.3",
      "se.scalablesolutions.akka" % "akka-amqp" % "1.1.3",
      "se.scalablesolutions.akka" % "akka-testkit" % "1.1.3"
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
