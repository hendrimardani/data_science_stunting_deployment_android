package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class AddingChildServiceByUserIdRequestJSON(
    @SerializedName("category_service_id") val categoryServiceId: Int,
    @SerializedName("catatan") val catatan: String? = null,
    @SerializedName("nama_anak") val namaAnak: String,
    @SerializedName("tinggi_cm") val tinggiCm: String,
    @SerializedName("hasil_pemeriksaan") val hasilPemeriksaan: String
)