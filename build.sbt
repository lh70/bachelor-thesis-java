import Dependencies.*

lazy val embedded = project.in(file("."))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    Settings.scalaVersion_3,
    libraryDependencies ++= List(
      upickle.value,
      "org.json" % "json" % "20211205",
    ),
    name            := "embedded",
  )
