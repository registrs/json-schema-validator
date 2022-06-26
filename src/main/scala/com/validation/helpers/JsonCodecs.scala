package com.validation.helpers

import com.validation.domain.ActionResult
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import shapeless.Unwrapped

object JsonCodecs {

  implicit def encodeAnyVal[T, U](
                                   implicit
                                   ev: T <:< AnyVal,
                                   unwrapped: Unwrapped.Aux[T, U],
                                   encoder: Encoder[U]
                                 ): Encoder[T] = Encoder.instance[T](value => encoder(unwrapped.unwrap(value)))

  implicit def dropNullsEncoder: Encoder[ActionResult] = deriveEncoder[ActionResult].mapJson(_.dropNullValues)

}
