package com.example.stunting.Database

import android.app.Application
import com.example.stunting.Database.Bumil.BumilDatabase
import com.example.stunting.Database.CalonPengantin.CalonPengantinDatabase
import com.example.stunting.Database.Child.AnakDatabase
import com.example.stunting.Database.LayananKeluarga.LayananKeluargaDatabase
import com.example.stunting.Database.RemajaPutri.RemajaPutriDatabase

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