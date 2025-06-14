package com.example.stunting.database.with_api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stunting.database.with_api.entities.branch.BranchDao
import com.example.stunting.database.with_api.entities.branch.BranchEntity
import com.example.stunting.database.with_api.entities.category_service.CategoryServiceDao
import com.example.stunting.database.with_api.entities.category_service.CategoryServiceEntity
import com.example.stunting.database.with_api.entities.checks.ChecksDao
import com.example.stunting.database.with_api.entities.checks.ChecksEntity
import com.example.stunting.database.with_api.entities.child_service.ChildServiceDao
import com.example.stunting.database.with_api.entities.child_service.ChildServiceEntity
import com.example.stunting.database.with_api.entities.children_patient.ChildrenPatientDao
import com.example.stunting.database.with_api.entities.children_patient.ChildrenPatientEntity
import com.example.stunting.database.with_api.entities.groups.GroupsDao
import com.example.stunting.database.with_api.entities.groups.GroupsEntity
import com.example.stunting.database.with_api.entities.messages.MessagesDao
import com.example.stunting.database.with_api.entities.messages.MessagesEntity
import com.example.stunting.database.with_api.entities.notifications.NotificationsDao
import com.example.stunting.database.with_api.entities.notifications.NotificationsEntity
import com.example.stunting.database.with_api.entities.pregnant_mom_service.PregnantMomServiceDao
import com.example.stunting.database.with_api.entities.pregnant_mom_service.PregnantMomServiceEntity
import com.example.stunting.database.with_api.entities.user_group.UserGroupDao
import com.example.stunting.database.with_api.entities.user_group.UserGroupEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileDao
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientDao
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity
import com.example.stunting.database.with_api.entities.users.UsersDao
import com.example.stunting.database.with_api.entities.users.UsersEntity

@Database(
    entities = [
        UsersEntity::class, UserProfileEntity::class, BranchEntity::class,
        UserProfilePatientEntity::class, ChildrenPatientEntity::class, CategoryServiceEntity::class,
        ChecksEntity::class, PregnantMomServiceEntity::class, ChildServiceEntity::class
                ],
    version = 57
)
abstract class ChattingDatabase: RoomDatabase() {
    abstract fun childServiceDao(): ChildServiceDao
    abstract fun pregnantMomServiceDao(): PregnantMomServiceDao
    abstract fun checksDao(): ChecksDao
    abstract fun categoryServiceDao(): CategoryServiceDao
    abstract fun childrenPatientDao(): ChildrenPatientDao
    abstract fun userProfilePatientDao(): UserProfilePatientDao
    abstract fun branchDao(): BranchDao
//    abstract fun userGroupDao(): UserGroupDao
//    abstract fun groupsDao(): GroupsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun usersDao(): UsersDao
//    abstract fun notificationsDao(): NotificationsDao
//    abstract fun messagesDao(): MessagesDao

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