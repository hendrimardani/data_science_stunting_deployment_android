package com.example.stunting.database

import android.app.Application

class DatabaseApp : Application() {
    val dbApp by lazy {
        LayananDatabase.getInstance(this)
    }
}