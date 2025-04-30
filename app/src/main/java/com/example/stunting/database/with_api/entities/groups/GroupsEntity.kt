package com.example.stunting.database.with_api.entities.groups

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_group") val id: Int? = null,
    @ColumnInfo(name = "nama_group") val namaGroup: String? = null,
    @ColumnInfo(name = "deskripsi") val deskripsi: String? = null,
    @ColumnInfo(name = "gambar_profile") val gambarProfile: String? = null,
    @ColumnInfo(name = "gambar_banner") val gambarBanner: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)