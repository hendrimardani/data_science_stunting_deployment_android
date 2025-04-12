package com.example.stunting.database.with_api.entities.notifications

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id_notification") val id: Int? = null,
    @ColumnInfo("is_status") val isStatus: Boolean? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)