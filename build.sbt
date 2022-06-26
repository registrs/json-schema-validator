ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

val Http4sVersion = "0.23.12"
val Specs2Version = "4.0.2"
val LogbackVersion = "1.2.11"
val JsonSchemaValidatorVersion = "2.2.14"
val doobieVersion = "1.0.0-RC2"
val enumeratumVersion = "1.7.0"
val circeVersion = "0.14.2"
val enumeratumCirceVersion = "1.7.0"
val sqlLiteVersion = "3.36.0.3"
val catsEffectVersion = "3.3.12"

lazy val root = (project in file("."))
  .settings(
    organization := "com.validation",
    name := "json-validation-service",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.github.java-json-tools" % "json-schema-validator" % JsonSchemaValidatorVersion,
      "org.xerial" % "sqlite-jdbc" % sqlLiteVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion, // HikariCP transactor.
      "org.tpolecat" %% "doobie-specs2" % doobieVersion, // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion, // ScalaTest support for typechecking statements.
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "com.beachape" %% "enumeratum" % enumeratumVersion
    ) ++ circeDependencies
  )

val circeDependencies = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion
)
