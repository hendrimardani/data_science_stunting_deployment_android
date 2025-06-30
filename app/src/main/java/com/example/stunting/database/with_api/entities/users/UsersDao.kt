package com.example.stunting.database.with_api.entities.users

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsersDao {

    @Query("SELECT * FROM users ORDER BY id_user ASC")
    fun getUsers(): LiveData<List<UsersEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UsersEntity>)
}