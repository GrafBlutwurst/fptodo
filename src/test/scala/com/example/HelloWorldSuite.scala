package com.example

import weaver._
import cats.effect._
import org.http4s.Status.Ok
import org.http4s._
import org.http4s.implicits._

object HelloWorldSuite extends SimpleIOSuite {

  val controller = new Main.Controller(Main.InMemoryRepository.todo()).routes

  test("hello world") {
    for {
      response <- controller.run(Request[IO](Method.GET, uri"/"))
    } yield expect(response.status == Ok)
  }

}
