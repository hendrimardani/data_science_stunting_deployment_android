package com.example.stunting.database.with_api.entities.branch

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity

@Entity(tableName = "branch")
data class BranchEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_branch") val id: Int? = null,
    @ColumnInfo(name = "nama_cabang") val namaCabang: String? = null,
    @ColumnInfo(name = "no_tlp") val noTlp: String? = null,
    @ColumnInfo(name = "alamat") val alamat: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)