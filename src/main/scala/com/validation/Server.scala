package com.validation

import cats.effect.{Async, ExitCode, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object Server {

  def stream[F[_] : Async](jsonValidationService: JsonValidationService[F]): Stream[F, Any] = {

    for {
      _ <- Stream.resource(EmberClientBuilder.default[F].build)
      httpApp = (
        Routes.jsonValidationRoutes[F](jsonValidationService) <+> Routes.jsonValidationRoutes2[F](jsonValidationService)
        ).orNotFound

      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

      exitCode <- Stream.resource(
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8090")
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never.as(ExitCode.Success))
      )

    } yield exitCode
  }.drain
}
