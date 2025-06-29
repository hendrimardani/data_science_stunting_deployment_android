package com.example.stunting.ui.nav_drawer_patient_fragment.user_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity
import com.example.stunting.database.with_api.response.DataUserProfilePatientsItem
import com.example.stunting.database.with_api.response.UpdateUserProfilePatientByIdResponse
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class NavUserProfilePatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // UserProfilePatient
    private var _getUserProfilePatientsFromApiResult = MutableLiveData<ResultState<List<DataUserProfilePatientsItem?>>>()
    val getUserProfilePatientsFromApiResult = _getUserProfilePatientsFromApiResult

    init {
        getUserProfilePatientsFromApi()
    }

    fun updateUserProfilePatientByIdFromLocal(userProfilePatientEntity: UserProfilePatientEntity) {
        viewModelScope.launch {
            chattingRepository.updateUserProfilePatientByIdFromLocal(userProfilePatientEntity)
        }
    }

    fun updateUserProfilePatientByIdFromApi(
        userPatientId: Int, namaCabang: String, namaBumil: String, nikBumil: String, tglLahirBumil: String,
        umurBumil: String, alamat: String, namaAyah: String
    ): LiveData<ResultState<UpdateUserProfilePatientByIdResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(
            chattingRepository.updateUserProfilePatientByIdFromApi(
                userPatientId, namaCabang, namaBumil, nikBumil, tglLahirBumil, umurBumil, alamat, namaAyah
            )
        )
    }

    fun getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId: Int) =
        chattingRepository.getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId)

    fun getUserProfilePatientsFromApi() {
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