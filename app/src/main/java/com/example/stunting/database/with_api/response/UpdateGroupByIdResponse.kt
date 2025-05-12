package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class UpdateGroupByIdResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("dataUpdateGroupById")
	val dataUpdateGroupById: List<DataUpdateGroupByIdItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataUpdateGroupByIdItem(

	@field:SerializedName("nama_group")
	val namaGroup: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("gambar_banner")
	val gambarBanner: String? = null,

	@field:SerializedName("gambar_profile")
	val gambarProfile: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null
) : Parcelable
