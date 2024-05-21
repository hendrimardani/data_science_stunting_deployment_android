package com.example.stunting.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    @Insert
    suspend fun insert(babyEntity: BabyEntity)

    @Update
    suspend fun update(babyEntity: BabyEntity)

    @Delete
    suspend fun delete(babyEntity: BabyEntity)

    @Query("SELECT * FROM `baby-table`")
    fun fetchAllbabies(): Flow<List<BabyEntity>>

    @Query("SELECT * FROM `baby-table` WHERE id=:id")
    fun fetchBabyById(id: Int):Flow<BabyEntity>
}