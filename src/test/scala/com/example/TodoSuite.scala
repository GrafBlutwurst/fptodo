package com.example

import com.example.model.Todo
import com.example.model.Todo._
import com.example.testhelper.ApiClient
import weaver._

object TodoSuite extends SimpleIOSuite {
  val controller = new Main.Controller(Main.InMemoryRepository.todo()).routes
  val apiClient = ApiClient.fromKleisli(controller)

  test("create and read") {
    val createRequest = () => apiClient.createTodo(Todo("todo"))
    for {
      create1response <- createRequest()
      create2response <- createRequest()
      expectedTodo1 = TodoWithId(TodoId("1"), Todo("todo"))
      _ <- expect(create1response == expectedTodo1).failFast
      _ <- apiClient
        .getTodo(TodoId("1"))
        .flatMap(todoWithId => expect(todoWithId == Some(expectedTodo1)).failFast)
      expectedTodo2 = TodoWithId(TodoId("2"), Todo("todo"))
      _ <- expect(create2response == expectedTodo2).failFast
      _ <- apiClient
        .getTodo(TodoId("2"))
        .flatMap(todoWithId => expect(todoWithId == Some(expectedTodo2)).failFast)
      allResponse <- apiClient.getTodos(())
    } yield expect(allResponse == List(expectedTodo1, expectedTodo2))
  }
}
