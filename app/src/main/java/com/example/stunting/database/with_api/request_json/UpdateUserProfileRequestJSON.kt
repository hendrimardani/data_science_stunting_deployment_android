package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class UpdateUserProfileByIdRequestJSON(
    val nama: String,
    val nik: String,
    val umur: Int,
    @SerializedName("jenis_kelamin") val jenisKelamin: String,
    @SerializedName("tglLahir") val tgl_lahir: String
)