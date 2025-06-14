package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllChildServicesResponse(

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("dataChildServices")
	val dataChildServices: List<DataChildServicesItem?>? = null
) : Parcelable

@Parcelize
data class DataChildServicesItem(

	@field:SerializedName("pemeriksaan_id")
	val pemeriksaanId: Int? = null,

	@field:SerializedName("hasil_pemeriksaan")
	val hasilPemeriksaan: String? = null,

	@field:SerializedName("tinggi_cm")
	val tinggiCm: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable
