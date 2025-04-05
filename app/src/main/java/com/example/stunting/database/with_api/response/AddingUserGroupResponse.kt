package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AddingUserGroupResponse(

	@field:SerializedName("dataUserGroup")
	val dataUserGroup: DataUserGroup? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataUserGroup(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("group_id")
	val groupId: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("user_profile_id")
	val userProfileId: Int? = null
) : Parcelable
