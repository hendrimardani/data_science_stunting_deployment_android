package com.example.stunting.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// If you change the structure database you have to change parameter version with increase
@Database(entities = [BabyEntity::class], version = 6)
abstract class BabyDatabase: RoomDatabase() {

    abstract fun babyDao(): BabyDao

    companion object {
        @Volatile
        private var INSTANCE:BabyDatabase? = null

        fun getInstance(context: Context): BabyDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BabyDatabase::class.java,
                        "baby_database"
                    ).fallbackToDestructiveMigration().build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}