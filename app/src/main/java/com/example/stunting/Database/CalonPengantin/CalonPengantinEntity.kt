package com.example.stunting.Database.CalonPengantin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calonPengantin-table")
data class CalonPengantinEntity(
    @PrimaryKey
    var tanggal: String,
    var namaCalonPengantin: String,
    var nikCalonPengantin: String,
    var tglLahirCalonPengantin: String,
    var umurCalonPengantin: String,
    var perkiraanTanggalPernikahanCalonPengantin: String,
    var periksaKesehatanCalonPengantin: String,
    var bimbinganPerkawinanCalonPengantin: String
)