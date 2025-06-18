package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class AddingPregnantMomServiceByUserIdRequestJSON(
    @SerializedName("category_service_id") val categoryServiceId: Int,
    @SerializedName("catatan") val catatan: String? = null,
    @SerializedName("nama_bumil") val namaBumil: String,
    @SerializedName("hari_pertama_haid_terakhir") val hariPertamaHaidTerakhir: String,
    @SerializedName("tgl_perkiraan_lahir") val tglPerkiraanLahir: String,
    @SerializedName("umur_kehamilan") val umurKehamilan: String,
    @SerializedName("status_gizi_kesehatan") val statusGiziKesehatan: String
)