package com.example.stunting.database.no_api.bumil

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bumil-table")
data class BumilEntity(
    @PrimaryKey
    var tanggal: String,
    var namaBumil: String,
    var nikBumil: String,
    var tglLahirBumil: String,
    var umurBumil: String,
    var hariPertamaHaidTerakhirBumil: String,
    var tanggalPerkiraanLahirBumil: String,
    var umurKehamilanBumil: String,
    var statusGiziKesehatanBumil: String
)