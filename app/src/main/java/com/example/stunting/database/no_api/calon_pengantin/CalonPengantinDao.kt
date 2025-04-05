package com.example.stunting.database.no_api.calon_pengantin

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

    @Query("DELETE FROM `calonPengantin-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `calonPengantin-table`")
    fun fetchAllCalonPengantin(): Flow<List<CalonPengantinEntity>>
}