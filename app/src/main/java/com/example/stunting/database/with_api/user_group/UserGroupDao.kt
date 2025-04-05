package com.example.stunting.database.with_api.user_group

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserGroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserGroup(userGroup: UserGroupEntity)

//    @Transaction
//    @Query("SELECT * FROM users")
//    fun getUserWithGroups(): LiveData<UserWithGroups>

    @Transaction
    @Query("SELECT * FROM groups")
    fun getGroupWithUsers(): LiveData<GroupWithUsers>
}