import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "TaWusel"
    val appVersion      = "0.1"

    val appDependencies = Seq(
      "mysql" % "mysql-connector-java" % "5.1.18",
      "com.typesafe.akka" % "akka-actor" % "2.+",
      "com.typesafe" %% "play-plugins-mailer" % "2.0.2"
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    )

}
