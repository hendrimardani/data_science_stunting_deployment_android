package com.example.stunting.database.with_api.entities.pregnant_mom_service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PregnantMomServiceDao {



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPregnantMomServices(pregnantMomServices: List<PregnantMomServiceEntity>)
}