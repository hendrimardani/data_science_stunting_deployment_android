package com.example.stunting.Database.CalonPengantin

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalonPengantinDao {

    @Insert
    suspend fun insert(calonPengantinEntity: CalonPengantinEntity)

    @Update
    suspend fun update(calonPengantinEntity: CalonPengantinEntity)

    @Delete
    suspend fun delete(calonPengantinEntity: CalonPengantinEntity)

    @Query("DELETE FROM `calonPengantin-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `calonPengantin-table`")
    fun fetchAllCalonPengantin(): Flow<List<CalonPengantinEntity>>

    @Query("SELECT * FROM `calonPengantin-table` WHERE tanggalCalonPengantin=:tanggal")
    fun fetchCalonPengantinById(tanggal: String):Flow<CalonPengantinEntity>
}