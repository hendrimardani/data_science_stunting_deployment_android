package com.example.stunting.database.with_api.entities.checks

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.stunting.database.with_api.entities.category_service.CategoryServiceEntity
import com.example.stunting.database.with_api.entities.children_patient.ChildrenPatientEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity

// Many to many
@Entity(
    tableName = "checks",
    primaryKeys = ["user_id", "children_patient_id", "category_service_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
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
    ]
)
data class ChecksEntity(
    val user_id: Int,
    val children_patient_id: Int,
    val category_service_id: Int,
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