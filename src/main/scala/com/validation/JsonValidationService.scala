package com.validation

import cats.implicits._
import cats.effect.Sync
import cats.implicits.catsSyntaxApplicativeId
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.validation.domain.Action.{UploadSchema, ValidateDocument}
import com.validation.domain.Status.{Error, Success}
import com.validation.domain.{ActionResult, SchemaContent, SchemaId}
import doobie.implicits._
import io.circe.Json
import io.circe.parser._

class JsonValidationService[F[_] : Sync](jsonValidationRepository: JsonValidationRepository[F]) {

  def getSchema(id: SchemaId): F[Option[String]] = {
    (for {
      schema <- jsonValidationRepository.getSchema(id)
        .onSqlException(new Exception("Sql error").raiseError[F, String])
    } yield schema)
      .handleError { e =>
        e.printStackTrace()
        None // we will return Not found in this case, but we "log" error at least
      }
  }

  def saveSchema(id: SchemaId, schemaContent: SchemaContent): F[ActionResult] = {
    (for {
      _ <- jsonValidationRepository.saveSchema(id, schemaContent)
        .onSqlException(new Exception("Sql error").raiseError[F, String])
    } yield ActionResult(UploadSchema, id, Success))
      .handleError { e =>
        ActionResult(UploadSchema, id, Error, Some(e.getMessage))
      }
  }

  def validateSchema(id: SchemaId, jsonToValidate: Json): F[ActionResult] = {
    (for {
      schema <- getSchema(id)
      schemaValue <- schema match {
        case Some(v) => v.pure[F]
        case None => new Exception("Schema was not found").raiseError[F, String]
      }
      _ <- validateJsonString(schemaValue)
    } yield {
      val factory = JsonSchemaFactory.byDefault()
      val validator = factory.getValidator
      val mapper = new ObjectMapper()
      val node = mapper.readTree(schemaValue)
      val report = validator.validate(node, mapper.readTree(jsonToValidate.toString))
      val (status, message) =
        if (report.isSuccess) (Success, None) else (Error, Some(report.iterator().next().getMessage))

      ActionResult(ValidateDocument, id, status, message)
    }).handleError { e =>
      ActionResult(ValidateDocument, id, Error, Some(e.getMessage))
    }

  }

  private def validateJsonString(jsonString: String): F[Unit] = {
    parse(jsonString) match {
      case Left(err) =>
        new Exception(s"failed to decode string: ${err.message}").raiseError[F, Unit]
      case Right(_) => ().pure[F]
    }
  }

}
