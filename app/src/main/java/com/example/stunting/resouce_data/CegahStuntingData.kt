package com.example.stunting.resouce_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CegahStuntingData(
    val title: String,
    val image: Int
): Parcelable