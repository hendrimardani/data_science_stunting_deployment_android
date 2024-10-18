package com.example.stunting.database.RemajaPutri

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// If you change the structure database you have to change parameter version with increase
@Database(entities = [RemajaPutriEntity::class], version = 1, exportSchema = true)
abstract class RemajaPutriDatabase: RoomDatabase() {

    abstract fun remajaPutriDao(): RemajaPutriDao

    companion object {

        @Volatile
        private var INSTANCE: RemajaPutriDatabase? = null

        fun getInstance(context: Context): RemajaPutriDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RemajaPutriDatabase::class.java,
                        "remajaPutri_database"
                    ).fallbackToDestructiveMigration().build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}