package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class UpdateGroupByIdRequestJSON(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("nama_group") val namaGroup: String,
    val deskripsi: String
)