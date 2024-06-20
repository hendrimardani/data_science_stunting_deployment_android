package com.example.stunting.Database.LayananKeluarga

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

    @Update
    suspend fun update(layananKeluargaEntity: LayananKeluargaEntity)

    @Delete
    suspend fun delete(layananKeluargaEntity: LayananKeluargaEntity)

    @Query("DELETE FROM `layananKeluarga-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `layananKeluarga-table`")
    fun fetchAllLayananKeluarga(): Flow<List<LayananKeluargaEntity>>

    @Query("SELECT * FROM `layananKeluarga-table` WHERE tanggal=:tanggal")
    fun fetchLayananKeluargaById(tanggal: String):Flow<LayananKeluargaEntity>
}