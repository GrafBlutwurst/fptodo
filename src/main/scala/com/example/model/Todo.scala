package com.example.model

final case class Todo(
  text: String
)

object Todo {
  final case class TodoId(value: String) extends AnyVal

  final case class TodoWithId(
    id: TodoId,
    todo: Todo
  )
}
