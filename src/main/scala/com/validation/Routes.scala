package com.validation

import cats.effect.{Concurrent, Sync}
import cats.implicits._
import com.validation.domain.{SchemaContent, SchemaId}
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import com.validation.helpers.JsonCodecs._

object Routes {

  def jsonValidationRoutes[F[_] : Sync](jsonValidationService: JsonValidationService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "schema" / id =>
        jsonValidationService.getSchema(SchemaId(id)).flatMap {
          case Some(value) => Ok(value)
          case None => NotFound(s"schema with id $id was not found")
        }
    }
  }

  def jsonValidationRoutes2[F[_] : Concurrent](jsonValidationService: JsonValidationService[F]): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {

      case req@POST -> Root / "validate" / id =>
        for {
          jsonToValidate <- req.as[Json]
          resp <- Ok(jsonValidationService.validateSchema(SchemaId(id), jsonToValidate))
        } yield resp

      case req@POST -> Root / "schema" / id =>
        for {
          schema <- req.as[Json]
          resp <- Created(jsonValidationService.saveSchema(SchemaId(id), SchemaContent(schema)))
        } yield resp

    }
  }

}
