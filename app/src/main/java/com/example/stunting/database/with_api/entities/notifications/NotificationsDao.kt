package com.example.stunting.database.with_api.entities.notifications

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationsDao {

    @Query("SELECT * FROM notifications ORDER BY id_notification ASC")
    fun getNotifications(): LiveData<List<NotificationsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNotifications(groups: List<NotificationsEntity>)
}