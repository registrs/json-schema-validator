package com.validation

import cats.effect.IO
import com.validation.domain.{SchemaContent, SchemaId}
import doobie.hikari.HikariTransactor
import doobie.implicits.toSqlInterpolator
import doobie.implicits._

case class JsonValidationRepositoryImpl(xa: HikariTransactor[IO]) extends JsonValidationRepository[IO] {

  override def getSchema(id: SchemaId): IO[Option[String]] =
    sql"""select content from json_validation_schemas where id = ${id.value}""".query[String].option.transact(xa)

  override def saveSchema(id: SchemaId, schemaContent: SchemaContent): IO[Int] = {
    val contentToInsert = schemaContent.content.toString()
    sql"""insert or replace into json_validation_schemas (id, content) values ($id, $contentToInsert)""".update.run.transact(xa)
  }

}
