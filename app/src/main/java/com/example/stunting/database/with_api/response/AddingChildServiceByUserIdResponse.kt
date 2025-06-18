package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AddingChildServiceByUserIdResponse(

	@field:SerializedName("dataLogin")
	val dataLogin: DataLoginItem? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataLoginUser(

	@field:SerializedName("umur")
	val umur: String? = null,

	@field:SerializedName("gambar_banner")
	val gambarBanner: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null,

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

@Parcelize
data class DataLoginItem(

	@field:SerializedName("dataLoginUser")
	val dataLoginUser: DataLoginUser? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("token")
	val token: String? = null
) : Parcelable
