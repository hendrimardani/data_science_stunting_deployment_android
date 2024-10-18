package com.example.stunting.database.Child

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// If you change the structure database you have to change parameter version with increase
@Database(entities = [AnakEntity::class], version = 1, exportSchema = true)
abstract class AnakDatabase: RoomDatabase() {

    abstract fun anakDao(): AnakDao

    companion object {

        @Volatile
        private var INSTANCE: AnakDatabase? = null

        fun getInstance(context: Context): AnakDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AnakDatabase::class.java,
                        "anak_database"
                    ).fallbackToDestructiveMigration().build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}