package com.example.stunting.database.with_api.entities.child_service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ChildServiceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChildServices(childServices: List<ChildServiceEntity>)
}