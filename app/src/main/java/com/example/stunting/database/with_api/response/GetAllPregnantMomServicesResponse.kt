package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllPregnantMomServicesResponse(

	@field:SerializedName("dataPregnantMomServices")
	val dataPregnantMomServices: List<DataPregnantMomServicesItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataPregnantMomServicesItem(

	@field:SerializedName("pemeriksaan_id")
	val pemeriksaanId: Int? = null,

	@field:SerializedName("status_gizi_kesehatan")
	val statusGiziKesehatan: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable
