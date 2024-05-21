package com.example.stunting.Database

import androidx.room.Entity

@Entity(tableName = "baby-table")
data class BabyEntity(
    var id: Int = 0,
    var umur: Int = 0,
    var jenisKelamin: String = "",
    var tinggi: Int = 0
)