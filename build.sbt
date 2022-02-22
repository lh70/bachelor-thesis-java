import Dependencies.*

lazy val sensorConnect = project.in(file("."))
  .settings(
    name := "sensorConnect",
    Settings.scalaVersion_3,
    libraryDependencies ++= List(
      upickle.value,
    ),
  )
