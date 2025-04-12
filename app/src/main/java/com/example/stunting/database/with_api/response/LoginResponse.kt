package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class LoginResponse(

	@field:SerializedName("dataLogin")
	val dataLogin: DataLogin? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataLoginUserProfile(

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("umur")
	val umur: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("jenis_kelamin")
	val jenisKelamin: String? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null
) : Parcelable

@Parcelize
data class DataLogin(

	@field:SerializedName("dataLoginUserProfile")
	val dataLoginUserProfile: DataLoginUserProfile? = null,

	@field:SerializedName("token")
	val token: String? = null
) : Parcelable
