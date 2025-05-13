package com.example.stunting.database.with_api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stunting.database.with_api.entities.groups.GroupsDao
import com.example.stunting.database.with_api.entities.groups.GroupsEntity
import com.example.stunting.database.with_api.entities.messages.MessagesDao
import com.example.stunting.database.with_api.entities.messages.MessagesEntity
import com.example.stunting.database.with_api.entities.notifications.NotificationsDao
import com.example.stunting.database.with_api.entities.notifications.NotificationsEntity
import com.example.stunting.database.with_api.entities.user_group.UserGroupDao
import com.example.stunting.database.with_api.entities.user_group.UserGroupEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileDao
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.entities.users.UsersDao
import com.example.stunting.database.with_api.entities.users.UsersEntity

@Database(
    entities = [
        UsersEntity::class, UserProfileEntity::class, GroupsEntity::class,
        UserGroupEntity::class, NotificationsEntity::class, MessagesEntity::class
                ],
    version = 33
)
abstract class ChattingDatabase: RoomDatabase() {
    abstract fun userGroupDao(): UserGroupDao
    abstract fun groupsDao(): GroupsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun usersDao(): UsersDao
    abstract fun notificationsDao(): NotificationsDao
    abstract fun messagesDao(): MessagesDao

    companion object {
        @Volatile
        private var INSTANCE: ChattingDatabase? = null

        fun getDatabase(context: Context): ChattingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChattingDatabase::class.java,
                    "chatting_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}