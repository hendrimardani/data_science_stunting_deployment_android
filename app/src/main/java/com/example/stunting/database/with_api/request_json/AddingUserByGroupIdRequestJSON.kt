package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class AddingUserByGroupIdRequestJSON(
    @SerializedName("user_id") val userId: List<Int>,
    val role: String?
)