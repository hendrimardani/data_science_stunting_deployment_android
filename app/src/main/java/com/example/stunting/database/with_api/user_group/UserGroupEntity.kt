package com.example.stunting.database.with_api.user_group

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.stunting.database.with_api.groups.GroupsEntity
import com.example.stunting.database.with_api.user_profile.UserProfileEntity

// Many to many
@Entity(
    tableName = "user_group",
    primaryKeys = ["id_group", "id_user_profile"],
    foreignKeys = [
        ForeignKey(
            entity = GroupsEntity::class,
            parentColumns = ["id_group"],
            childColumns = ["id_group"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id_user_profile"],
            childColumns = ["id_user_profile"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserGroupEntity(
    // Foreign key
    val id_group: Int,
    // Foreign key
    val id_user_profile: Int,
    val role: String?  = null,
    @ColumnInfo(name = "created_by") val createdBy: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedaT: String? = null
)

data class UserProfileWithGroups(
    @Embedded val userProfile: UserProfileEntity,

    @Relation(
        parentColumn = "id_user_profile",
        entityColumn = "id_group",
        associateBy = Junction(UserGroupEntity::class)
    )

    val groups: List<GroupsEntity?>
)

data class GroupWithUserProfiles(
    @Embedded val group: GroupsEntity,

    @Relation(
        parentColumn = "id_group",
        entityColumn = "id_user_profile",
        associateBy = Junction(UserGroupEntity::class)
    )

    val usersProfile: List<UserProfileEntity?>
)