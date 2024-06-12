package com.example.stunting.Database.CalonPengantin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// If you change the structure database you have to change parameter version with increase
@Database(entities = [CalonPengantinEntity::class], version = 1, exportSchema = true)
abstract class CalonPengantinDatabase: RoomDatabase() {

    abstract fun calonPengantinDao(): CalonPengantinDao

    companion object {

        @Volatile
        private var INSTANCE: CalonPengantinDatabase? = null

        fun getInstance(context: Context): CalonPengantinDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CalonPengantinDatabase::class.java,
                        "calonPengantin_database"
                    ).fallbackToDestructiveMigration().build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}