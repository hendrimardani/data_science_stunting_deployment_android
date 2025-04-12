package com.example.stunting.database.with_api.user_group

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.example.stunting.database.with_api.groups.GroupsEntity
import com.example.stunting.database.with_api.user_profile.UserProfileEntity

// Many to many
@Entity(
    tableName = "user_group",
    primaryKeys = ["id_group", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = GroupsEntity::class,
            parentColumns = ["id_group"],
            childColumns = ["id_group"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserGroupEntity(
    // Foreign key
    val id_group: Int,
    // Foreign key
    val user_id: Int,
    val role: String?  = null,
    @ColumnInfo(name = "created_by") val createdBy: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)

data class UserProfileWithGroups(
    @Embedded val userProfile: UserProfileEntity,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id_group",
        associateBy = Junction(UserGroupEntity::class)
    )

    val groups: List<GroupsEntity?>
)

data class GroupWithUserProfiles(
    @Embedded val group: GroupsEntity,

    @Relation(
        parentColumn = "id_group",
        entityColumn = "user_id",
        associateBy = Junction(UserGroupEntity::class)
    )

    val usersProfile: List<UserProfileEntity?>
)