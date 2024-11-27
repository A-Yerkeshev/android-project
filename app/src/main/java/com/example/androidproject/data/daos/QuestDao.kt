package com.example.androidproject.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidproject.data.models.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    // Adds new quest
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(questEntity: QuestEntity): Long

    // Gets all quests
    @Query("SELECT * FROM quests")
    fun getAllQuests(): Flow<List<QuestEntity>>

    // Gets quest by id
    @Query("select * from quests where id = :id")
    fun getById(id: Int): Flow<List<QuestEntity>>

    // Gets last created quest
    @Query("select * from quests order by id desc limit 1")
    fun getLast(): Flow<List<QuestEntity>>

    // Updates quest
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(questEntity: QuestEntity): Int

    // Deletes quest
    @Delete
    suspend fun delete(questEntity: QuestEntity): Int

    // Marks all quests as not current. This is necessary to ensure that only one quest is current at a time.
    @Query("update quests set current = 0")
    fun unsetAllCurrent()

    // Gets all quests
    @Query("select * from quests")
    fun getAll(): Flow<List<QuestEntity>>

    // Gets current quest. Only one quest can be current at a time.
    @Query("select * from quests where current = 1")
    fun getCurrent(): Flow<List<QuestEntity>>

    // Gets completed quests
    @Query("SELECT * FROM quests WHERE completed_at IS NOT NULL")
    fun getCompletedQuests(): Flow<List<QuestEntity>>
}