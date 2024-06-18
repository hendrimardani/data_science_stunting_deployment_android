package com.example.stunting.Database.Child

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnakDao {

    @Insert
    suspend fun insert(anakEntity: AnakEntity)

    @Update
    suspend fun update(anakEntity: AnakEntity)

    @Delete
    suspend fun delete(anakEntity: AnakEntity)

    @Query("DELETE FROM `anak-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `anak-table`")
    fun fetchAllAnak(): Flow<List<AnakEntity>>

    @Query("SELECT * FROM `anak-table` WHERE tanggal=:tanggal")
    fun fetchAnakById(tanggal: String):Flow<AnakEntity>
}