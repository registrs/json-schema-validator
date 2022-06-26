package com.validation.domain

import enumeratum.EnumEntry.LowerCamelcase
import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait Action extends EnumEntry with LowerCamelcase

object Action extends Enum[Action]
  with CirceEnum[Action] {

  case object UploadSchema extends Action

  case object ValidateDocument extends Action

  def values: IndexedSeq[Action] = findValues
}
