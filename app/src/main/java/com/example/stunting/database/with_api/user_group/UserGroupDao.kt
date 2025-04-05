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
    @Query("SELECT * FROM user_group WHERE id_user_profile = :userId")
    fun getUserProfilesWithGroupsByUserProfileId(userId: Int): LiveData<UserGroupEntity>

    @Transaction
    @Query("SELECT * FROM user_profile")
    fun getUserProfilesWithGroups(): LiveData<UserProfileWithGroups>

    @Transaction
    @Query("SELECT * FROM groups")
    fun getGroupWithUserProfiles(): LiveData<GroupWithUserProfiles>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserGroup(userGroup: UserGroupEntity)
}