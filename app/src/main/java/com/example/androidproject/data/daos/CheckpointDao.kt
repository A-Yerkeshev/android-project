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
    // Adds new checkpoint
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkpointEntity: CheckpointEntity): Long

    // Gets checkpoint by id
    @Query("select * from checkpoints where id = :id")
    fun getById(id: Int): Flow<List<CheckpointEntity>>

    // Gets last created checkpoint
    @Query("select * from checkpoints order by id desc limit 1")
    fun getLast(): Flow<List<CheckpointEntity>>

    // Gets all checkpoints
    @Query("select * from checkpoints order by quest_id, id asc")
    fun getAllCheckpoints(): Flow<List<CheckpointEntity>>

    // Gets all checkpoints for a particular quest
    @Query("select * from checkpoints where quest_id = :questId")
    fun getAll(questId: Int): Flow<List<CheckpointEntity>>

    // Updates checkpoint
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(checkpointEntity: CheckpointEntity): Int

    // Deletes checkpoint
    @Delete
    suspend fun delete(checkpointEntity: CheckpointEntity): Int

    // Gets checkpoints, associated with the current quest
    @Query("select * from checkpoints where quest_id in (select id from quests where current = 1)")
    fun getCurrent(): Flow<List<CheckpointEntity>>
}