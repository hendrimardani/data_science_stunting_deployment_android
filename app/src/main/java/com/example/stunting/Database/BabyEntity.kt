package com.example.stunting.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby-table")
data class BabyEntity(
    @PrimaryKey
    var tanggal: String,
    var umur: String = "",
    var jenisKelamin: String = "",
    var tinggi: String = "",
    var klasifikasi: String = ""
)