package com.minimal.todolist.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val dao: TodoDao) {

    val allTodos: Flow<List<Todo>> = dao.getAll()

    suspend fun add(text: String): Todo {
        val todo = Todo(text = text)
        val id = dao.insert(todo)
        return todo.copy(id = id.toInt())
    }

    suspend fun complete(id: Int) {
        val todo = dao.getById(id) ?: return
        dao.update(todo.copy(isDone = true, completedAt = System.currentTimeMillis()))
    }

    suspend fun uncomplete(id: Int) {
        val todo = dao.getById(id) ?: return
        dao.update(todo.copy(isDone = false, completedAt = null))
    }

    suspend fun delete(todo: Todo) {
        dao.delete(todo)
    }

    suspend fun getById(id: Int): Todo? = dao.getById(id)
}
