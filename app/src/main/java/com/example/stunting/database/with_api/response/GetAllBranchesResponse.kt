package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllBranchesResponse(

	@field:SerializedName("dataBranches")
	val dataBranches: List<DataBranchesItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataBranchesItem(

	@field:SerializedName("no_tlp")
	val noTlp: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("nama_cabang")
	val namaCabang: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null
) : Parcelable
