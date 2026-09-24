package com.minimal.todolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY isDone ASC, createdAt DESC")
    fun getAll(): Flow<List<Todo>>

    @Insert
    suspend fun insert(todo: Todo): Long

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Todo?

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteById(id: Int)
}
