package com.example.stunting.database.with_api.entities.category_service

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_service")
data class CategoryServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_category_service") val id: Int? = null,
    @ColumnInfo(name = "nama_layanan") val namaLayanan: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)