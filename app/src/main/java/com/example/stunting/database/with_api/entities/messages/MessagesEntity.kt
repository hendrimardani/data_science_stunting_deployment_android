package com.example.stunting.database.with_api.entities.messages

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.stunting.database.with_api.entities.groups.GroupsEntity
import com.example.stunting.database.with_api.entities.notifications.NotificationsEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity

// Many to many
@Entity(
    tableName = "messages",
    primaryKeys = ["user_id", "group_id", "notification_id"],
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
        ),
        ForeignKey(
            entity = NotificationsEntity::class,
            parentColumns = ["id_notification"],
            childColumns = ["notification_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessagesEntity(
    val user_id: Int,
    val group_id: Int,
    val notification_id: Int,
    val isi_pesan: String?  = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)

data class MessagesRelation(
    @Embedded(prefix = "up_") val userProfileEntity: UserProfileEntity,
    @Embedded(prefix = "g_") val groupsEntity: GroupsEntity,
    @Embedded(prefix = "n_") val notificationsEntity: NotificationsEntity,
    @Embedded(prefix = "m_") val messagesEntity: MessagesEntity,
)