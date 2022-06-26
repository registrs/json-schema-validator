package com.validation.domain

import io.circe.Json

final case class SchemaContent(content: Json) extends AnyVal
