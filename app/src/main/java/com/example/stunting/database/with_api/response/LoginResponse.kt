package com.example.stunting.database.with_api.response

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("dataLogin")
	val dataLogin: DataLogin? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PasienUser(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_patient_id")
	val userPatientId: Int? = null,

	@field:SerializedName("nama_bumil")
	val namaBumil: String? = null,

	@field:SerializedName("tgl_lahir_bumil")
	val tglLahirBumil: String? = null,

	@field:SerializedName("nama_ayah")
	val namaAyah: String? = null,

	@field:SerializedName("nik_bumil")
	val nikBumil: String? = null,

	@field:SerializedName("umur_bumil")
	val umurBumil: String? = null,

	@field:SerializedName("gambar_banner")
	val gambarBanner: String? = null,

	@field:SerializedName("gambar_profile")
	val gambarProfile: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,
)

data class PetugasUser(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("jenis_kelamin")
	val jenisKelamin: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("umur")
	val umur: String? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("gambar_banner")
	val gambarBanner: String? = null,

	@field:SerializedName("gambar_profile")
	val gambarProfile: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,
)

data class DataLogin(

	@field:SerializedName("dataLoginUser")
	val dataLoginUser: JsonObject? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
