package com.example.stunting.database.with_api.entities.checks

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.stunting.database.with_api.entities.category_service.CategoryServiceEntity
import com.example.stunting.database.with_api.entities.children_patient.ChildrenPatientEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity

// Many to many
@Entity(
    tableName = "checks",
    primaryKeys = ["id_pemeriksaan", "user_id", "user_patient_id", "children_patient_id", "category_service_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserProfilePatientEntity::class,
            parentColumns = ["user_patient_id"],
            childColumns = ["user_patient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChildrenPatientEntity::class,
            parentColumns = ["id_children_patient"],
            childColumns = ["children_patient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryServiceEntity::class,
            parentColumns = ["id_category_service"],
            childColumns = ["category_service_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_pemeriksaan"], unique = true)]
)
data class ChecksEntity(
    @ColumnInfo(name = "id_pemeriksaan") val id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "user_patient_id") val userPatientId: Int,
    @ColumnInfo(name = "children_patient_id") val childrenPatientId: Int,
    @ColumnInfo(name = "category_service_id") val categoryServiceId: Int,
    @ColumnInfo(name = "tgl_pemeriksaan") val tglPemeriksaan: String?  = null,
    val catatan: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)

data class ChecksRelation(
    @Embedded(prefix = "up_") val userProfileEntity: UserProfileEntity,
    @Embedded(prefix = "cp_") val childrenPatientEntity: ChildrenPatientEntity,
    @Embedded(prefix = "cs_") val categoryServiceEntity: CategoryServiceEntity,
    @Embedded(prefix = "c_") val checksEntity: ChecksEntity
)