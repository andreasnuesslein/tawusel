import sbt._
import Keys._
import PlayProject._
import de.johoop.jacoco4sbt.JacocoPlugin._

object ApplicationBuild extends Build {

    val appName         = "TaWusel"
    val appVersion      = "0.1"

      lazy val s = Defaults.defaultSettings ++ Seq(jacoco.settings:_*)
      
    val appDependencies = Seq(
      "mysql" % "mysql-connector-java" % "5.1.18",
      "com.typesafe.akka" % "akka-actor" % "2.+",
      "com.typesafe" %% "play-plugins-mailer" % "2.0.2",
      "com.novocode" % "junit-interface" % "0.8" % "test->default",
      "org.scalatest" %% "scalatest" % "1.6.1" % "test",
      "org.springframework" % "spring-mock" % "2.0.8"
      "com.codahale" %% "jerkson" % "0.5.0"
    )


    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA, settings= s).settings(
     		// Add your own project settings here
 		parallelExecution in jacoco.Config := false
    )

}
