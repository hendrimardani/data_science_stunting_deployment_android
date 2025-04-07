package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class AddingMessageRequestJSON(
    @SerializedName("isi_pesan") val isiPesan: String
)
