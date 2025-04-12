package com.example.stunting.database.with_api.user_profile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stunting.database.with_api.users.UsersEntity

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserProfile(userProfile: List<UserProfileEntity>)

    @Query("SELECT * FROM users WHERE id_user = :userId")
    fun getUserProfileById(userId: Int): LiveData<UsersEntity>

    @Transaction
    @Query("SELECT * FROM users WHERE id_user = :userId")
    fun getUserWithUserProfileById(userId: Int): LiveData<UserWithUserProfile>

    @Query("SELECT * FROM user_profile ORDER BY id_user_profile ASC")
    fun getUserProfiles(): LiveData<List<UserProfileEntity>>
}