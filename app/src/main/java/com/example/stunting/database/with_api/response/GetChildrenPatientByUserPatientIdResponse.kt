package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetChildrenPatientByUserPatientIdResponse(

	@field:SerializedName("dataChildrenPatientByUserPatientId")
	val dataChildrenPatientByUserPatientId: List<DataChildrenPatientByUserPatientIdItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataChildrenPatientByUserPatientIdItem(

	@field:SerializedName("nama_anak")
	val namaAnak: String? = null,

	@field:SerializedName("user_patient_id")
	val userPatientId: Int? = null,

	@field:SerializedName("umur_anak")
	val umurAnak: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("nik_anak")
	val nikAnak: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("tgl_lahir_anak")
	val tglLahirAnak: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("jenis_kelamin_anak")
	val jenisKelaminAnak: String? = null
) : Parcelable
