package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllMessagesResponse(

	@field:SerializedName("dataMessages")
	val dataMessages: List<DataMessagesItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class Notifications(

	@field:SerializedName("is_status")
	val isStatus: Boolean? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable

@Parcelize
data class UserProfileItemMessages(

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("umur")
	val umur: String? = null,

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
data class DataMessagesItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("group_id")
	val groupId: Int? = null,

	@field:SerializedName("isi_pesan")
	val isiPesan: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("groups")
	val groups: Groups? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("notification_id")
	val notificationId: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: UserProfile? = null,

	@field:SerializedName("user_profile_id")
	val userProfileId: Int? = null,

	@field:SerializedName("notifications")
	val notifications: Notifications? = null
) : Parcelable

@Parcelize
data class GroupsItemMessages(

	@field:SerializedName("nama_group")
	val namaGroup: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null
) : Parcelable
