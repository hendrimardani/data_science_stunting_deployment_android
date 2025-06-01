package com.example.stunting.database.with_api.entities.user_profile_patient

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.stunting.database.with_api.entities.branch.BranchEntity
import com.example.stunting.database.with_api.entities.users.UsersEntity

@Entity(
    tableName = "user_profile_patient",
    foreignKeys = [
        ForeignKey(
            entity = UsersEntity::class,
            parentColumns = ["id_user"],
            childColumns = ["user_patient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id_branch"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.CASCADE
        )
                  ],
    indices = [Index(value = ["user_patient_id"], unique = true)]
)
data class UserProfilePatientEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_user_profile_patient") val id: Int? = null,
    @ColumnInfo(name = "user_patient_id") val userPatientId: Int? = null,
    @ColumnInfo(name = "branch_id") val branchId: Int? = null,
    @ColumnInfo(name = "nama_bumil") val namaBumil: String? = null,
    @ColumnInfo(name = "nik_bumil") val nikBumil: String? = null,
    @ColumnInfo(name = "tgl_lahir_bumil") val tglLahirBumil: String? = null,
    @ColumnInfo(name = "umur_bumil") val umurBumil: String? = null,
    @ColumnInfo(name = "alamat") val alamat: String? = null,
    @ColumnInfo(name = "nama_ayah") val namaAyah: String? = null,
    @ColumnInfo(name = "gambar_profile") val gambarProfile: String? = null,
    @ColumnInfo(name = "gambar_banner") val gambarBanner: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)

// One to one
data class UserProfilePatientWithUserRelation(
    @Embedded val users: UsersEntity,

    @Relation(
        parentColumn = "id_user",
        entityColumn = "user_patient_id"
    )
    val userProfilePatient: UserProfilePatientEntity?
)

// Many to one
data class UserProfilePatientsWithBranchRelation(
    @Embedded val branch: BranchEntity,

    @Relation(
        parentColumn = "id_branch",
        entityColumn = "branch_id"
    )
    val userProfilePatients: List<UserProfilePatientEntity?>
)