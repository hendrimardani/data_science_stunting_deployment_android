package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class UpdateUserProfilePatientByIdRequestJSON(
    @SerializedName("nama_cabang") val namaCabang: String,
    @SerializedName("nama_bumil") val namaBumil: String,
    @SerializedName("nik_bumil") val nikBumil: String,
    @SerializedName("tgl_lahir_bumil") val tglLahirBumil: String,
    @SerializedName("umur_bumil") val umurBumil: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("nama_ayah") val namaAyah: String
)