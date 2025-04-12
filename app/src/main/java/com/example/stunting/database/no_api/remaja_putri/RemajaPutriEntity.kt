package com.example.stunting.database.no_api.remaja_putri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remajaPutri-table")
data class RemajaPutriEntity(
    @PrimaryKey
    var tanggal: String,
    var namaRemajaPutri: String,
    var nikRemajaPutri: String,
    var tglLahirRemajaPutri: String,
    var umurRemajaPutri: String,
    var mendapatTtdRemajaPutri: String,
    var periksaAnemiaRemajaPutri: String,
    var hasilPeriksaAnemiaRemajaPutri: String
)