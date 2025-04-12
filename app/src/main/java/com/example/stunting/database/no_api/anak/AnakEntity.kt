package com.example.stunting.database.no_api.anak

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anak-table")
data class AnakEntity(
    @PrimaryKey
    var tanggal: String,
    var namaAnak: String,
    var jkAnak: String,
    var nikAnak: String,
    var tglLahirAnak: String,
    var umurAnak: String,
    var tinggiAnak: String,
    var ortuAnak: String,
    var klasifikasiAnak: String,
    var pencegahanStunting: String
)