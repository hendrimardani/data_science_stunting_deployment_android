package com.example.stunting.Database

import android.app.Application

class BabyApp : Application() {
    val db by lazy {
        BabyDatabase.getInstance(this)
    }
}