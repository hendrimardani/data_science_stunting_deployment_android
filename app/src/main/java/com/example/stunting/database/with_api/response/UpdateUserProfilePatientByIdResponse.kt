package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class UpdateUserProfilePatientByIdResponse(

	@field:SerializedName("dataUpdateUserProfilePatientById")
	val dataUpdateUserProfilePatientById: DataUpdateUserProfilePatientById? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataUpdateUserProfilePatientById(

	@field:SerializedName("tgl_lahir_bumil")
	val tglLahirBumil: String? = null,

	@field:SerializedName("gambar_banner")
	val gambarBanner: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("umur_bumil")
	val umurBumil: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("user_patient_id")
	val userPatientId: Int? = null,

	@field:SerializedName("nama_bumil")
	val namaBumil: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("branch_id")
	val branchId: Int? = null,

	@field:SerializedName("gambar_profile")
	val gambarProfile: String? = null,

	@field:SerializedName("nama_ayah")
	val namaAyah: String? = null,

	@field:SerializedName("nik_bumil")
	val nikBumil: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable
