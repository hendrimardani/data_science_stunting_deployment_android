package com.example.stunting.Database.Bumil

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bumil-table")
data class BumilEntity(
    @PrimaryKey
    var tanggalBumil: String,
    var namaBumil: String,
    var nikBumil: String,
    var tglLahirBumil: String,
    var umurBumil: String,
    var hariPertamaHaidTerakhirBumil: String,
    var tanggalPerkiraanLahirBumil: String,
    var umurKehamilanBumil: String,
    var statusGiziKesehatanBumil: String
)