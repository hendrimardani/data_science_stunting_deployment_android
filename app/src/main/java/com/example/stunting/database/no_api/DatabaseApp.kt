package com.example.stunting.database.no_api

import android.app.Application

class DatabaseApp : Application() {
    val dbApp by lazy {
        LayananDatabase.getInstance(this)
    }
}