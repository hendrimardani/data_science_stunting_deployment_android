package com.example.stunting.database

import android.app.Application
import com.example.stunting.database.Bumil.BumilDatabase
import com.example.stunting.database.CalonPengantin.CalonPengantinDatabase
import com.example.stunting.database.Child.AnakDatabase
import com.example.stunting.database.LayananKeluarga.LayananKeluargaDatabase
import com.example.stunting.database.RemajaPutri.RemajaPutriDatabase

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