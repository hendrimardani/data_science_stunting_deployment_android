package com.example.stunting.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby-table")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var umur: String = "",
    var jenisKelamin: String = "",
    var tinggi: String = ""
)