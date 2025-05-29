package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllUserProfilePatientsResponse(

	@field:SerializedName("dataUserProfilePatients")
	val dataUserProfilePatients: List<DataUserProfilePatientsItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class UsersItem(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
) : Parcelable

@Parcelize
data class BranchItem(

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

@Parcelize
data class DataUserProfilePatientsItem(

	@field:SerializedName("tgl_lahir_bumil")
	val tglLahirBumil: String? = null,

	@field:SerializedName("gambar_banner")
	val gambarBanner: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("branch")
	val branch: Branch? = null,

	@field:SerializedName("users")
	val users: Users? = null,

	@field:SerializedName("umur_bumil")
	val umurBumil: String? = null,

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
