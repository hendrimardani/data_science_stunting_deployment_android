package com.example.stunting.datastore.chatting

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val id: String,
    val nama: String,
    val role: String,
    val token: String,
    val isLogin: Boolean = false,
    val isUpdateUserProfilePatient: Boolean = false
): Parcelable