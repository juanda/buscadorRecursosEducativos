import com.github.play2war.plugin._

name := "recursos"

version := "1.0-SNAPSHOT"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.0"

resolvers += "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  //"org.reactivemongo" %% "reactivemongo" % "0.10.0"
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)     

play.Project.playScalaSettings
