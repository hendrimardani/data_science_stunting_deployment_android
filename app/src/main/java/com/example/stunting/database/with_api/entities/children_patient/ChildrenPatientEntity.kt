package com.example.stunting.database.with_api.entities.children_patient

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity

@Entity(
    tableName = "children_patient",
    foreignKeys = [
        ForeignKey(
            entity = UserProfilePatientEntity::class,
            parentColumns = ["user_patient_id"],
            childColumns = ["user_patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_children_patient"], unique = true)]
)
data class ChildrenPatientEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_children_patient") val id: Int? = null,
    @ColumnInfo(name = "user_patient_id") val userPatientId: Int? = null,
    @ColumnInfo(name = "nama_anak") val namaAnak: String? = null,
    @ColumnInfo(name = "nik_anak") val nikAnak: String? = null,
    @ColumnInfo(name = "jenis_kelamin_anak") val jenisKelaminAnak: String? = null,
    @ColumnInfo(name = "tgl_lahir_anak") val tglLahirAnak: String? = null,
    @ColumnInfo(name = "umur_anak") val umurAnak: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
