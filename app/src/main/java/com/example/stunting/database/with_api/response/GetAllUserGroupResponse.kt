package com.example.stunting.database.with_api.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllUserGroupResponse(

	@field:SerializedName("userGroups")
	val userGroups: List<UserGroupsItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class UserGroupsItem(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("group_id")
	val groupId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("groups")
	val groups: Groups? = null,

	@field:SerializedName("user_profile")
	val userProfile: UserProfile? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("user_profile_id")
	val userProfileId: Int? = null
) : Parcelable

@Parcelize
data class Groups(

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

@Parcelize
data class UserProfile(

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
	val tglLahir: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null
) : Parcelable
