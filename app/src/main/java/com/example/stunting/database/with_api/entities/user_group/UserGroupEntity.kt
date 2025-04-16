package com.example.stunting.database.with_api.entities.user_group

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.example.stunting.database.with_api.entities.groups.GroupsEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity

// Many to many
@Entity(
    tableName = "user_group",
    primaryKeys = ["user_id", "group_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GroupsEntity::class,
            parentColumns = ["id_group"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserGroupEntity(
    val user_id: Int,
    val group_id: Int,
    val role: String?  = null,
    @ColumnInfo(name = "created_by") val createdBy: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)

data class UserGroupRelation(
    @Embedded(prefix = "up_") val userProfileEntity: UserProfileEntity,
    @Embedded(prefix = "g_") val groupsEntity: GroupsEntity,
    @Embedded(prefix = "ug_") val userGroupEntity: UserGroupEntity
)
