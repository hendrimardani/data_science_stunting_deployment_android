package com.example.stunting.database.with_api.users

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUsers(users: List<UsersEntity>)

    @Query("SELECT * FROM users ORDER BY id_user ASC")
    fun getUsers(): LiveData<List<UsersEntity>>
}