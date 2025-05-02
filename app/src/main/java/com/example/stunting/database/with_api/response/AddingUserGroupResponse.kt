package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddingUserGroupResponse(
	val dataUserGroup: List<DataUserGroupItem?>? = null,
	val message: String? = null,
	val status: String? = null
) : Parcelable

@Parcelize
data class DataUserGroupItem(
	val role: String? = null,
	val updatedAt: String? = null,
	val userId: Int? = null,
	val groupId: Int? = null,
	val createdAt: String? = null,
	val id: Int? = null,
	val createdBy: String? = null
) : Parcelable
