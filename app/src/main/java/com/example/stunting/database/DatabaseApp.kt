package com.example.stunting.database

import android.app.Application
import com.example.stunting.database.bumil.BumilDatabase
import com.example.stunting.database.calon_pengantin.CalonPengantinDatabase
import com.example.stunting.database.anak.AnakDatabase
import com.example.stunting.database.layanan_keluarga.LayananKeluargaDatabase
import com.example.stunting.database.remaja_putri.RemajaPutriDatabase

class DatabaseApp : Application() {
    val dbChildDatabase by lazy {
        AnakDatabase.getInstance(this)
    }
    val dbBumilDatabase by lazy {
        BumilDatabase.getInstance(this)
    }
    val dbCalonPengantinDatabase by lazy {
        CalonPengantinDatabase.getInstance(this)
    }
    val dbRemajaPutriDatabase by lazy {
        RemajaPutriDatabase.getInstance(this)
    }
    val dbLayananKeluargaDatabase by lazy {
        LayananKeluargaDatabase.getInstance(this)
    }
}