package com.minimal.todolist.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.minimal.todolist.data.Todo
import com.minimal.todolist.data.TodoRepository
import com.minimal.todolist.notification.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository,
    private val appContext: Context
) : ViewModel() {

    val todos: StateFlow<List<Todo>> = repository.allTodos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Keep the system notifications perfectly in sync with the DB at all times,
        // so state (add/complete/delete) survives app restarts and is reflected
        // immediately no matter where the change came from (app or overlay).
        viewModelScope.launch {
            todos.collect { list ->
                NotificationHelper.refreshAll(appContext, list)
            }
        }
    }

    fun addTodo(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.add(trimmed)
        }
    }

    fun completeTodo(todo: Todo) {
        viewModelScope.launch {
            repository.complete(todo.id)
        }
    }

    fun uncompleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.uncomplete(todo.id)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
            NotificationHelper.cancel(appContext, todo.id)
        }
    }
}

class TodoViewModelFactory(
    private val repository: TodoRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TodoViewModel(repository, context.applicationContext) as T
    }
}
