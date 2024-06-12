package com.example.stunting.Database.Child

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anak-table")
data class AnakEntity(
    @PrimaryKey
    var tanggalAnak: String,
    var namaAnak: String,
    var jkAnak: String,
    var nikAnak: String,
    var tglLahirAnak: String,
    var umurAnak: String,
    var tinggiAnak: String,
    var ortuAnak: String,
    var klasifikasiAnak: String
)