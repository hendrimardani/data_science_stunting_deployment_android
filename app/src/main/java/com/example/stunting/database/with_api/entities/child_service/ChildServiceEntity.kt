package com.example.stunting.database.with_api.entities.child_service

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stunting.database.with_api.entities.checks.ChecksEntity

@Entity(
    tableName = "child_service",
    foreignKeys = [
        ForeignKey(
            entity = ChecksEntity::class,
            parentColumns = ["id_pemeriksaan"],
            childColumns = ["pemeriksaan_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_child_service"], unique = true)]
)
data class ChildServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_child_service") val id: Int? = null,
    @ColumnInfo(name = "pemeriksaan_id") val pemeriksaanId: Int? = null,
    @ColumnInfo(name = "tinggi_cm") val tinggiCm: String? = null,
    @ColumnInfo(name = "hasil_pemeriksaan") val hasilPemeriksaan: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)