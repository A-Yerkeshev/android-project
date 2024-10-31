package com.example.androidproject.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidproject.data.models.CheckpointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckpointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkpointEntity: CheckpointEntity): Long

    @Query("select * from checkpoints where id = :id")
    fun getById(id: Int): Flow<List<CheckpointEntity>>

    @Query("select * from checkpoints order by id desc limit 1")
    fun getLast(): Flow<List<CheckpointEntity>>

    @Query("select * from checkpoints where quest_id = :questId")
    fun getAll(questId: Int): Flow<List<CheckpointEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(checkpointEntity: CheckpointEntity): Int

    @Delete
    suspend fun delete(checkpointEntity: CheckpointEntity): Int
}