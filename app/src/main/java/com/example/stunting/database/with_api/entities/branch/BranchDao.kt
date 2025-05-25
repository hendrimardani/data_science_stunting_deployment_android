package com.example.stunting.database.with_api.entities.branch

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BranchDao {

    @Query("SELECT * FROM branch ORDER BY id_branch ASC")
    fun getBranches(): LiveData<List<BranchEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBranch(userProfile: List<BranchEntity>)
}