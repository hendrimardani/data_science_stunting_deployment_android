package com.example.stunting.database.with_api.groups

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGroups(groups: List<GroupsEntity>)

    @Query("SELECT * FROM groups ORDER BY id_group ASC")
    fun getGroups(): LiveData<List<GroupsEntity>>
}