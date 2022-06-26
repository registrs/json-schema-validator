package com.validation.domain

/** Case class for json result of action done
 *
 * @param action - action performed
 * @param id - id of schema action was performed with
 * @param status - status of action (Success or Error)
 * @param message - message in case of error */
case class ActionResult(action: Action, id: SchemaId, status: Status, message: Option[String] = None)
