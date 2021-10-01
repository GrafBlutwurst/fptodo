package com.example.testhelper

import cats.data.Kleisli
import cats.effect.{ Concurrent, Resource }
import endpoints4s.http4s.client.Endpoints
import fs2.Chunk
import org.http4s._
import org.http4s.client.Client
import org.http4s.implicits._
import cats.implicits._
import com.example.DomainEndpoints

object ApiClient {

  def fromKleisli[M[_]](
    runRequest: Kleisli[M, Request[M], Response[M]]
  )(implicit M: Concurrent[M]) =
    new Endpoints[M](
      uri"",
      Client[M] { req =>
        Resource.eval(
          for {
            memLoadedReq <- memLoadRequest(req)
            resp <- runRequest.run(memLoadedReq)
            memLoadedResp <- memLoadResponse(resp)
          } yield memLoadedResp
        )
      }
    ) with DomainEndpoints with endpoints4s.http4s.client.JsonEntitiesFromSchemas

  private def memLoadRequest[M[_]: Concurrent](
    request: Request[M]
  ): M[Request[M]] =
    request.body.compile
      .to(Chunk)
      .map(bodyBytes => request.withBodyStream(fs2.Stream.chunk(bodyBytes)))

  private def memLoadResponse[M[_]: Concurrent](
    response: Response[M]
  ): M[Response[M]] =
    response.body.compile
      .to(Chunk)
      .map(bodyBytes =>
        response.copy(
          body = fs2.Stream.chunk(bodyBytes)
        )
      )
}
