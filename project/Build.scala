import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "TaxiMob"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "mysql" % "mysql-connector-java" % "5.1.18",
      "com.typesafe.akka" % "akka-actor" % "2.+"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    )

}
