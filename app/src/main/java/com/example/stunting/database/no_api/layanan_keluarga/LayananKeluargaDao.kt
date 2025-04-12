package com.example.stunting.database.no_api.layanan_keluarga

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LayananKeluargaDao {

    @Insert
    suspend fun insert(layananKeluargaEntity: LayananKeluargaEntity)

    @Query("DELETE FROM `layananKeluarga-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `layananKeluarga-table`")
    fun fetchAllLayananKeluarga(): Flow<List<LayananKeluargaEntity>>
}