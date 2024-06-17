package com.example.stunting.Database.Bumil

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BumilDao {

    @Insert
    suspend fun insert(bumilEntity: BumilEntity)

    @Update
    suspend fun update(bumilEntity: BumilEntity)

    @Delete
    suspend fun delete(bumilEntity: BumilEntity)

    @Query("DELETE FROM `bumil-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `bumil-table`")
    fun fetchAllBumil(): Flow<List<BumilEntity>>

    @Query("SELECT * FROM `bumil-table` WHERE tanggalBumil=:tanggal")
    fun fetchBumilById(tanggal: String):Flow<BumilEntity>
}