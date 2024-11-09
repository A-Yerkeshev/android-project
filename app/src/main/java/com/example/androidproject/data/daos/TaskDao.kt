package com.example.androidproject.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidproject.data.models.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskEntity: TaskEntity): Long

    @Query("select * from tasks where id = :id")
    fun getById(id: Int): Flow<List<TaskEntity>>

    @Query("select * from tasks order by id desc limit 1")
    fun getLast(): Flow<List<TaskEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(taskEntity: TaskEntity): Int

    @Delete
    suspend fun delete(taskEntity: TaskEntity): Int

    @Query("select * from tasks where checkpoint_id in" +
            "(select id from checkpoints where quest_id in" +
            "(select id from quests where current = 1))")
    fun getCurrent(): Flow<List<TaskEntity>>
}