package com.example.stunting.database.LayananKeluarga

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// If you change the structure database you have to change parameter version with increase
@Database(entities = [LayananKeluargaEntity::class], version = 1, exportSchema = true)
abstract class LayananKeluargaDatabase: RoomDatabase() {

    abstract fun layananKeluargaDao(): LayananKeluargaDao

    companion object {

        @Volatile
        private var INSTANCE: LayananKeluargaDatabase? = null

        fun getInstance(context: Context): LayananKeluargaDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LayananKeluargaDatabase::class.java,
                        "layananKeluarga_database"
                    ).fallbackToDestructiveMigration().build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}