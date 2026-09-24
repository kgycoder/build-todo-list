package com.minimal.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
