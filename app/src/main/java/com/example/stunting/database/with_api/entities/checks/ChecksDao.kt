package com.example.stunting.database.with_api.entities.checks

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChecksDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChecks(checks: List<ChecksEntity>)
}