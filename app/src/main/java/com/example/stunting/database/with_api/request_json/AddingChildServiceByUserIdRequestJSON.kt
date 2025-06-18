package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class AddingChildServiceByUserIdRequestJSON(
    @SerializedName("category_service_id") val categoryServiceId: Int,
    @SerializedName("catatan") val catatan: String? = null,
    @SerializedName("nama_anak") val namaAnak: String,
    @SerializedName("nik_anak") val nikAnak: String,
    @SerializedName("jenis_kelamin_anak") val jenisKelaminAnak: String,
    @SerializedName("tglLahirAnak") val tglLahirAnak: String,
    @SerializedName("umur_anak") val umur_anak: String,
    @SerializedName("tinggi_cm") val tinggiCm: String,
    @SerializedName("hasil_pemeriksaan") val hasilPemeriksaan: String
)