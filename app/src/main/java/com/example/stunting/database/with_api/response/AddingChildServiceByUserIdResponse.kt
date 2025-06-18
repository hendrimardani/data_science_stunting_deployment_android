package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AddingChildServiceByUserIdResponse(

	@field:SerializedName("dataChildService")
	val dataChildService: DataChildService? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class Checks(

	@field:SerializedName("user_patient_id")
	val userPatientId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("category_service_id")
	val categoryServiceId: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("tgl_pemeriksaan")
	val tglPemeriksaan: String? = null,

	@field:SerializedName("catatan")
	val catatan: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("children_patient_id")
	val childrenPatientId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable

@Parcelize
data class DataChildService(

	@field:SerializedName("pemeriksaan_id")
	val pemeriksaanId: Int? = null,

	@field:SerializedName("hasil_pemeriksaan")
	val hasilPemeriksaan: String? = null,

	@field:SerializedName("tinggi_cm")
	val tinggiCm: String? = null,

	@field:SerializedName("checks")
	val checks: Checks? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable
