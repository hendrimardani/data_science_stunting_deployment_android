package com.example.stunting.database.with_api.entities.user_profile

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stunting.database.with_api.entities.branch.BranchEntity
import com.example.stunting.database.with_api.entities.users.UsersEntity

@Entity(
    tableName = "user_profile",
    foreignKeys = [
        ForeignKey(
            entity = UsersEntity::class,
            parentColumns = ["id_user"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id_branch"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.CASCADE
        )
                  ],
    indices = [Index(value = ["user_id"], unique = true)] // One-to-one: unique index
)
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_user_profile") val id: Int? = null,
    @ColumnInfo(name = "user_id") val userId: Int? = null,
    @ColumnInfo(name = "branch_id") val branchId: Int? = null,
    @ColumnInfo(name = "nama") val nama: String? = null,
    @ColumnInfo(name = "nik") val nik: String? = null,
    @ColumnInfo(name = "jenis_kelamin") val jenisKelamin: String? = null,
    @ColumnInfo(name = "tgl_lahir") val tglLahir: String? = null,
    @ColumnInfo(name = "umur") val umur: String? = null,
    @ColumnInfo(name = "alamat") val alamat: String? = null,
    @ColumnInfo(name = "gambar_profile") val gambarProfile: String? = null,
    @ColumnInfo(name = "gambar_banner") val gambarBanner: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)

// Nama nya harus UserProfileWithUserRelation
data class UserProfileWithUserRelation(
    @Embedded(prefix = "b_") val branch: BranchEntity,
    @Embedded(prefix = "u_") val users: UsersEntity,
    @Embedded(prefix = "up_") val userProfile: UserProfileEntity
)

