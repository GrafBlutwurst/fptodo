package com.example

import cats.effect.kernel.Async
import cats.effect.{ ExitCode, IO, IOApp }
import com.example.model.Todo.{ TodoId, TodoWithId }
import com.example.model.{ Todo, TodoRepository }
import org.http4s.{ HttpApp, HttpRoutes }
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.CORS

object Main extends IOApp with Http4sDsl[IO] {
  override def run(args: List[String]): IO[ExitCode] = {
    object config {
      val port = 8000
    }

    val controller = new Controller(InMemoryRepository.todo[IO]())

    BlazeServerBuilder[IO](scala.concurrent.ExecutionContext.global)
      .bindHttp(config.port, "0.0.0.0")
      .withHttpApp(CORS(controller.routes))
      .serve
      .compile
      .last
      .map(_.getOrElse(ExitCode(-1)))
  }

  class Controller(todoRepository: TodoRepository[IO])
      extends endpoints4s.http4s.server.Endpoints[IO]
      with endpoints4s.http4s.server.JsonEntitiesFromSchemas
      with DomainEndpoints {
    val routes: HttpApp[IO] = HttpRoutes
      .of(
        routesFromEndpoints(
          helloWorld.implementedBy(_ => "Hello world!"),
          getTodo.implementedByEffect { todoId =>
            todoRepository.findById(todoId)
          },
          getTodos.implementedByEffect { _ =>
            todoRepository.all()
          },
          createTodo.implementedByEffect { todo =>
            todoRepository.create(todo)
          }
        )
      )
      .orNotFound
  }

  object InMemoryRepository {
    def todo[M[_]]()(implicit M: Async[M]): TodoRepository[M] =
      new TodoRepository[M] {

        private val todos = scala.collection.mutable.Map.empty[TodoId, Todo]

        override def all(): M[List[TodoWithId]] =
          M.delay {
            todos.view.toList.map((TodoWithId.apply _).tupled)
          }

        override def create(todo: Todo): M[TodoWithId] =
          M.delay {
            val id = TodoId((todos.size + 1).toString)
            todos.put(id, todo)
            TodoWithId(id, todo)
          }

        override def findById(id: TodoId): M[Option[TodoWithId]] =
          M.delay {
            todos.get(id).map(TodoWithId(id, _))
          }
      }
  }
}
