package com.example.stunting.database.with_api.entities.user_profile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserProfileDao {

    @Transaction
    @Query("SELECT * FROM users WHERE id_user = :userId")
    fun getUserProfileWithUserById(userId: Int): LiveData<UserProfileWithUserRelation>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUserProfileWithUserRelationFromLocal(): LiveData<List<UserProfileWithUserRelation>>

    @Query("SELECT * FROM user_profile ORDER BY id_user_profile ASC")
    fun getUserProfilesFromLocal(): LiveData<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProfiles(userProfiles: List<UserProfileEntity>)
}