package com.example.stunting.database.with_api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stunting.database.with_api.entities.groups.GroupsDao
import com.example.stunting.database.with_api.entities.groups.GroupsEntity
import com.example.stunting.database.with_api.entities.user_group.UserGroupDao
import com.example.stunting.database.with_api.entities.user_group.UserGroupEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileDao
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.entities.users.UsersDao
import com.example.stunting.database.with_api.entities.users.UsersEntity

@Database(
    entities = [
        UsersEntity::class, UserProfileEntity::class, GroupsEntity::class,
        UserGroupEntity::class
                ],
    version = 27
)
abstract class ChattingDatabase: RoomDatabase() {
    abstract fun userGroupDao(): UserGroupDao
    abstract fun groupsDao(): GroupsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun usersDao(): UsersDao

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