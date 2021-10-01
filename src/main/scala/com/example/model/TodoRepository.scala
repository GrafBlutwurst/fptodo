package com.example.model

import com.example.model.Todo.{ TodoId, TodoWithId }

trait TodoRepository[M[_]] {
  def all(): M[List[TodoWithId]]
  def create(todo: Todo): M[TodoWithId]
  def findById(id: TodoId): M[Option[TodoWithId]]
}

object TodoRepository {}
