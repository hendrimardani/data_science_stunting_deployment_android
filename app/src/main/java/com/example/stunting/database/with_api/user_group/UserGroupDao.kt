package com.example.stunting.database.with_api.user_group

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stunting.database.with_api.user_profile.UserProfileEntity

@Dao
interface UserGroupDao {

    // Query ini tidak ke API melainkan ke database
    @Transaction
    @Query("SELECT * FROM user_group WHERE user_id = :userId")
    fun getUserGroupByUserProfileId(userId: Int): LiveData<List<UserGroupEntity>>

    @Query("SELECT * FROM user_group ORDER BY user_id ASC")
    fun getUserGroup(): LiveData<List<UserGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserGroups(userGroups: List<UserGroupEntity>)
}