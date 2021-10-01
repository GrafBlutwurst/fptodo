package com.example

import com.example.model.Todo
import com.example.model.Todo.{ TodoId, TodoWithId }
import endpoints4s.{ algebra, generic }

trait DomainEndpoints extends algebra.Endpoints with algebra.JsonEntitiesFromSchemas with generic.JsonSchemas {

  implicit lazy val todoIdSchema: JsonSchema[TodoId] =
    defaultStringJsonSchema.xmap(TodoId.apply)(_.value)

  implicit lazy val todoSchema: Record[Todo] =
    field[String]("text")
      .xmap(Todo.apply)(_.text)

  implicit lazy val todoWithIdSchema: Record[TodoWithId] =
    field[TodoId]("id")
      .zip(
        todoSchema
      )
      .xmap((TodoWithId.apply _).tupled) { case TodoWithId(id, todo) =>
        (id, todo)
      }

  val helloWorld: Endpoint[Unit, String] =
    endpoint(
      get(path),
      ok(textResponse)
    )

  val getTodo: Endpoint[TodoId, Option[TodoWithId]] =
    endpoint(
      get(path / "todo" / segment[String]("todo").xmap(TodoId.apply)(_.value)),
      wheneverFound(ok(jsonResponse[TodoWithId]))
    )

  val getTodos: Endpoint[Unit, List[TodoWithId]] =
    endpoint(
      get(path / "todo"),
      ok(jsonResponse[List[TodoWithId]])
    )

  val createTodo: Endpoint[Todo, TodoWithId] =
    endpoint(
      post(
        path / "todo",
        jsonRequest[Todo]
      ),
      ok(jsonResponse[TodoWithId])
    )

}
