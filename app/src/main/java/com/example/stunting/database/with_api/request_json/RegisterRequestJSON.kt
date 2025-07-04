package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class RegisterRequestJSON(
    val nama: String,
    val email: String,
    @SerializedName("nama_cabang") val namaCabang: String,
    val password: String,
    @SerializedName("repeat_password") val repeatPassword: String
)