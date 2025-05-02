package com.example.stunting.database.with_api.entities.user_profile

data class UserProfileWithSelection(
    val userProfileWithUserRelation: UserProfileWithUserRelation,
    var isSelected: Boolean = false
)
