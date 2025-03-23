package com.example.stunting.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stunting.database.anak.AnakDao
import com.example.stunting.database.anak.AnakEntity
import com.example.stunting.database.bumil.BumilDao
import com.example.stunting.database.bumil.BumilEntity
import com.example.stunting.database.calon_pengantin.CalonPengantinDao
import com.example.stunting.database.calon_pengantin.CalonPengantinEntity
import com.example.stunting.database.layanan_keluarga.LayananKeluargaDao
import com.example.stunting.database.layanan_keluarga.LayananKeluargaEntity
import com.example.stunting.database.message_chatbot.MessageChatbotDao
import com.example.stunting.database.message_chatbot.MessageChatbotEntity
import com.example.stunting.database.remaja_putri.RemajaPutriDao
import com.example.stunting.database.remaja_putri.RemajaPutriEntity

// If you change the structure database you have to change parameter version with increase
@Database(entities = [
                        AnakEntity::class, BumilEntity::class, CalonPengantinEntity::class,
                        LayananKeluargaEntity::class, MessageChatbotEntity::class, RemajaPutriEntity::class
                     ], version = 2, exportSchema = true)
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
                    ).build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }
}