package com.example.stunting.Database.RemajaPutri

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RemajaPutriDao {

    @Insert
    suspend fun insert(remajaPutriEntity: RemajaPutriEntity)

    @Update
    suspend fun update(calonPengantinEntity: RemajaPutriEntity)

    @Delete
    suspend fun delete(remajaPutriEntity: RemajaPutriEntity)

    @Query("DELETE FROM `remajaPutri-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `remajaPutri-table`")
    fun fetchAllbabies(): Flow<List<RemajaPutriEntity>>

    @Query("SELECT * FROM `remajaPutri-table` WHERE tanggalRemajaPutri=:tanggal")
    fun fetchBabyById(tanggal: String):Flow<RemajaPutriEntity>
}