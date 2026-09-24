package com.minimal.todolist

import android.app.Application
import com.minimal.todolist.data.TodoDatabase
import com.minimal.todolist.data.TodoRepository
import com.minimal.todolist.notification.NotificationHelper

class TodoApplication : Application() {

    val database by lazy { TodoDatabase.getInstance(this) }
    val repository by lazy { TodoRepository(database.todoDao()) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
