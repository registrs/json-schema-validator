package com.validation

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp, Resource}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import doobie.implicits.toSqlInterpolator
import doobie.implicits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    transactor.use { xa =>
      createInitialDbSchema(xa)
      val repo = JsonValidationRepositoryImpl(xa)
      val service = new JsonValidationService(repo)
      Server.stream[IO](service).compile.drain.as(ExitCode.Success)
    }
  }

  private val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.sqlite.JDBC",
        "jdbc:sqlite:validator.db",
        "sa",
        "",
        ec
      )
    } yield xa

  private def createInitialDbSchema(xa: HikariTransactor[IO]): Unit =
    sql"""
    CREATE TABLE IF NOT EXISTS json_validation_schemas (
      id TEXT NOT NULL UNIQUE,
      content TEXT NOT NULL NULL
    )
  """.update.run.transact(xa).unsafeRunAndForget()

}
