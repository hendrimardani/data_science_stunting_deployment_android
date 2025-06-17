package com.example.stunting.database.with_api.response

import com.google.gson.annotations.SerializedName

data class AddingPregnantMomServiceResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("dataPregnantMomService")
	val dataPregnantMomService: DataPregnantMomService? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataPregnantMomService(

	@field:SerializedName("pemeriksaan_id")
	val pemeriksaanId: Int? = null,

	@field:SerializedName("umur_kehamilan")
	val umurKehamilan: String? = null,

	@field:SerializedName("status_gizi_kesehatan")
	val statusGiziKesehatan: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("hari_pertama_haid_terakhir")
	val hariPertamaHaidTerakhir: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("tgl_perkiraan_lahir")
	val tglPerkiraanLahir: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
