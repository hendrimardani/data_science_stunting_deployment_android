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
    // ChildrenPatient
    private var _getChildrenPatientByUserPatientIdFromApiResult = MutableLiveData<ResultState<List<DataChildrenPatientByUserPatientIdItem?>>>()
    // UserProfile
    private var _getUserProfilesFromApiResult = MutableLiveData<ResultState<List<DataUserProfilesItem?>>>()
    val getUserProfilesFromApiResult = _getUserProfilesFromApiResult

    // UserProfilePatient
    private var _getUserProfilePatientsFromApiResult = MutableLiveData<ResultState<List<DataUserProfilePatientsItem?>>>()
    val getUserProfilePatientsFromApiResult = _getUserProfilePatientsFromApiResult

    init {
        getUserProfilesFromApi()
        getUserProfilePatientFromApi()
    }

    fun getChildrenPatientByUserPatientIdFromApi(userPatientId: Int) {
        viewModelScope.launch {
            _getChildrenPatientByUserPatientIdFromApiResult.value = ResultState.Loading
            _getChildrenPatientByUserPatientIdFromApiResult.value = chattingRepository.getChildrenPatientByUserPatientIdFromApi(userPatientId)
        }
    }

    private fun getUserProfilesFromApi() {
        viewModelScope.launch {
            _getUserProfilesFromApiResult.value = ResultState.Loading
            _getUserProfilesFromApiResult.value = chattingRepository.getUserProfilesFromApi()
        }
    }

    fun getUserProfilePatientWithUserRelationByIdFromLocal(userPatientId: Int) =
        chattingRepository.getUserProfilePatientWithUserRelationByIdFromLocal(userPatientId)

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