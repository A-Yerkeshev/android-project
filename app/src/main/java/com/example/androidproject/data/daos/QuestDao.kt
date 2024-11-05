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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(questEntity: QuestEntity): Long

    @Query("SELECT * FROM quests")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("select * from quests where id = :id")
    fun getById(id: Int): Flow<List<QuestEntity>>

    @Query("select * from quests order by id desc limit 1")
    fun getLast(): Flow<List<QuestEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(questEntity: QuestEntity): Int

    @Delete
    suspend fun delete(questEntity: QuestEntity): Int

    @Query("update quests set current = 0")
    fun unsetAllCurrent()
}