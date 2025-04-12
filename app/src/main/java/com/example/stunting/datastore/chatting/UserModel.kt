package com.example.stunting.datastore.chatting

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var id: String,
    var email: String,
    var token: String,
    var isLogin: Boolean = false
): Parcelable