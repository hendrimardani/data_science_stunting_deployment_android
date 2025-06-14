package com.example.stunting.database.with_api.request_json

import com.google.gson.annotations.SerializedName

data class AddingChildrenPatientByUserPatientIdRequestJSON(
    @SerializedName("nama_anak") val namaAnak: String,
    @SerializedName("nik_anak") val nikAnak: String,
    @SerializedName("jenis_kelamin_anak") val jenisKelaminAnak: String,
    @SerializedName("tgl_lahir_anak") val tglLahirAnak: String,
    @SerializedName("umur_anak") val umurAnak: String,

)