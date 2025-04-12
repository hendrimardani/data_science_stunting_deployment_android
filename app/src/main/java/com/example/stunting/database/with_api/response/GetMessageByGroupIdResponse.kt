package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetMessageByGroupIdResponse(

	@field:SerializedName("dataMessagesByGroupId")
	val dataMessagesByGroupId: List<DataMessagesByGroupIdItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataMessagesByGroupIdItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("group_id")
	val groupId: Int? = null,

	@field:SerializedName("isi_pesan")
	val isiPesan: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("notification_id")
	val notificationId: Int? = null,

	@field:SerializedName("user_profile_id")
	val userProfileId: Int? = null
) : Parcelable
