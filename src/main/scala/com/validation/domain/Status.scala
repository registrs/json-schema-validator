package com.validation.domain

import enumeratum.EnumEntry.LowerCamelcase
import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait Status extends EnumEntry with LowerCamelcase

object Status extends Enum[Status] with CirceEnum[Status] {

  case object Success extends Status

  case object Error extends Status

  def values: IndexedSeq[Status] = findValues
}
