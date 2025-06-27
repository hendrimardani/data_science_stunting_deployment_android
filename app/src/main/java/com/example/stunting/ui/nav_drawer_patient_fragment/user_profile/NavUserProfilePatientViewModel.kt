package com.example.stunting.ui.nav_drawer_patient_fragment.user_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientsWithBranchRelation
import com.example.stunting.datastore.chatting.ChattingRepository

class NavUserProfilePatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    fun getUserProfilePatientWithUserRelationByIdFromLocal(userPatientId: Int) =
        chattingRepository.getUserProfilePatientWithUserRelationByIdFromLocal(userPatientId)

    fun getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId: Int) =
        chattingRepository.getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId)
}