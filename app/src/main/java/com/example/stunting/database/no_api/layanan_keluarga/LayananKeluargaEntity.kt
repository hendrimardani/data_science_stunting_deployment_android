package com.example.stunting.database.no_api.layanan_keluarga

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "layananKeluarga-table")
data class LayananKeluargaEntity(
    @PrimaryKey
    var tanggal: String,
    var namaLayananKeluarga: String,
    var dusunLayananKeluarga: String,
    var namaLengkapIbuHamilLayananKeluarga: String,
    var anakLayananKeluarga: String,
    var kategoriKeluargaLayananKeluarga: String,
    var memilikiKartuKeluargaLayananKeluarga: String,
    var memilikiJambanSehatLayananKeluarga: String,
    var memilikiSumberAirBersihLayananKeluarga:String,
    var pesertaJaminanSosialLayananKeluarga: String,
    var memilikiAksesSanitasiPembuanganLimbahLayakLayananKeluarga: String,
    var pendampinganKeluargaOlehTpkLayananKeluarga: String,
    var pesertaKegiatanKetahananPanganLayananKeluarga: String
)