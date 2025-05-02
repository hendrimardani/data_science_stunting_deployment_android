package com.example.stunting.database.with_api.entities.groups

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupsDao {

    @Query("DELETE FROM groups")
    suspend fun deleteGroups()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroups(groups: List<GroupsEntity>)
}