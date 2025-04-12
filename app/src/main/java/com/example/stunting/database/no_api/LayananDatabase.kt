package com.example.stunting.database.no_api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stunting.database.no_api.anak.AnakDao
import com.example.stunting.database.no_api.anak.AnakEntity
import com.example.stunting.database.no_api.bumil.BumilDao
import com.example.stunting.database.no_api.bumil.BumilEntity
import com.example.stunting.database.no_api.calon_pengantin.CalonPengantinDao
import com.example.stunting.database.no_api.calon_pengantin.CalonPengantinEntity
import com.example.stunting.database.no_api.layanan_keluarga.LayananKeluargaDao
import com.example.stunting.database.no_api.layanan_keluarga.LayananKeluargaEntity
import com.example.stunting.database.with_api.entities.message_chatbot.MessageChatbotDao
import com.example.stunting.database.with_api.entities.message_chatbot.MessageChatbotsEntity
import com.example.stunting.database.no_api.remaja_putri.RemajaPutriDao
import com.example.stunting.database.no_api.remaja_putri.RemajaPutriEntity

@Database(entities = [
                        AnakEntity::class, BumilEntity::class, CalonPengantinEntity::class,
                        LayananKeluargaEntity::class, MessageChatbotsEntity::class, RemajaPutriEntity::class
                     ], version = 4)
abstract class LayananDatabase: RoomDatabase() {
    abstract fun anakDao(): AnakDao
    abstract fun bumilDao(): BumilDao
    abstract fun calonPengantinDao(): CalonPengantinDao
    abstract fun layananKeluargaDao(): LayananKeluargaDao
    abstract fun messageChatbotDao(): MessageChatbotDao
    abstract fun remajaPutriDao(): RemajaPutriDao

    companion object {

        @Volatile
        private var INSTANCE: LayananDatabase? = null

        fun getInstance(context: Context): LayananDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LayananDatabase::class.java,
                        "layanan_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}