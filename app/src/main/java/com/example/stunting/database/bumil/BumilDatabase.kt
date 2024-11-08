package com.example.stunting.database.bumil

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// If you change the structure database you have to change parameter version with increase
@Database(entities = [BumilEntity::class], version = 4, exportSchema = true)
abstract class BumilDatabase: RoomDatabase() {

    abstract fun bumilDao(): BumilDao

    companion object {

        @Volatile
        private var INSTANCE: BumilDatabase? = null

        fun getInstance(context: Context): BumilDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BumilDatabase::class.java,
                        "bumil_database"
                    ).fallbackToDestructiveMigration().build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}