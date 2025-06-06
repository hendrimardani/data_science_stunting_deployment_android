package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllUserProfilesResponse(

	@field:SerializedName("dataUserProfiles")
	val dataUserProfiles: List<DataUserProfilesItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class Branch(

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
data class Users(

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
data class DataUserProfilesItem(

	@field:SerializedName("umur")
	val umur: String? = null,

	@field:SerializedName("gambar_banner")
	val gambarBanner: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("branch")
	val branch: Branch? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null,

	@field:SerializedName("users")
	val users: Users? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("branch_id")
	val branchId: Int? = null,

	@field:SerializedName("gambar_profile")
	val gambarProfile: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("jenis_kelamin")
	val jenisKelamin: String? = null
) : Parcelable
