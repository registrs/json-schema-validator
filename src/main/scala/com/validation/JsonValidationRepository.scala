package com.validation

import com.validation.domain.{SchemaContent, SchemaId}

trait JsonValidationRepository[F[_]] {

  def getSchema(id: SchemaId): F[Option[String]]

  def saveSchema(id: SchemaId, schemaContent: SchemaContent): F[Int]

}
