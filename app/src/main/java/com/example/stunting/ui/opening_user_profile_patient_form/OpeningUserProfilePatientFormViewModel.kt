package com.example.stunting.ui.opening_user_profile_patient_form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientWithBranchesRelation
import com.example.stunting.database.with_api.response.DataUserProfilePatientsItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class OpeningUserProfilePatientFormViewModel(
    private val chattingRepository: ChattingRepository
): ViewModel() {

    private var _getUserProfilePatientsResult = MutableLiveData<ResultState<List<DataUserProfilePatientsItem?>>>()
    val getUserProfilePatientsResult: LiveData<ResultState<List<DataUserProfilePatientsItem?>>> = _getUserProfilePatientsResult

    val getUserProfilePatientWithBranchesRelationFromLocal: LiveData<List<UserProfilePatientWithBranchesRelation>> =
        chattingRepository.getUserProfilePatientWithBranchesRelationFromLocal()

    init {
        getUserProfilePatientsFromApi()
    }

    private fun getUserProfilePatientsFromApi() {
        viewModelScope.launch {
            _getUserProfilePatientsResult.value = ResultState.Loading
            _getUserProfilePatientsResult.value = chattingRepository.getUserProfilePatientsFromApi()
        }
    }
}