package com.example.stunting.database.with_api.user_group

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserGroupDao {

    // Query ini tidak ke API melainkan ke database
    @Transaction
    @Query("SELECT * FROM user_profile WHERE user_id = :userId")
    fun getUserProfileWithGroupsByUserProfileId(userId: Int): LiveData<List<UserProfileWithGroups>>

    @Transaction
    @Query("SELECT * FROM user_profile")
    fun getUserProfileWithGroups(): LiveData<List<UserProfileWithGroups>>

    @Transaction
    @Query("SELECT * FROM groups")
    fun getGroupWithUserProfiles(): LiveData<List<GroupWithUserProfiles>>

    @Query("SELECT * FROM user_group ORDER BY user_id ASC")
    fun getUserGroup(): LiveData<List<UserGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserGroups(userGroups: List<UserGroupEntity>)
}