package com.example.stunting.ui.nav_drawer_patient_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataChildrenPatientByUserPatientIdItem
import com.example.stunting.database.with_api.response.DataUserProfilePatientsItem
import com.example.stunting.database.with_api.response.DataUserProfilesItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class NavDrawerMainActivityPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // UserProfilePatient
    private var _getUserProfilePatientsFromApiResult = MutableLiveData<ResultState<List<DataUserProfilePatientsItem?>>>()
    val getUserProfilePatientsFromApiResult = _getUserProfilePatientsFromApiResult

    init {
        getUserProfilePatientFromApi()
    }

    fun getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId: Int) =
        chattingRepository.getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId)

    private fun getUserProfilePatientFromApi() {
        viewModelScope.launch {
            _getUserProfilePatientsFromApiResult.value = ResultState.Loading
            _getUserProfilePatientsFromApiResult.value = chattingRepository.getUserProfilePatientsFromApi()
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}