package com.example.stunting.ui.opening_user_profile_patient_form

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

class OpeningUserProfilePatientFormViewModel(
    private val chattingRepository: ChattingRepository
): ViewModel() {
    private var _updateUserProfilePatientById = MutableLiveData<ResultState<UpdateUserProfilePatientByIdResponse?>>()
    val updateUserProfilePatientById = _updateUserProfilePatientById
    private var _getUserProfilePatientsResult = MutableLiveData<ResultState<List<DataUserProfilePatientsItem?>>>()
    val getUserProfilePatientsResult = _getUserProfilePatientsResult

    init {
        getUserProfilePatientsFromApi()
    }

    fun updateUserProfilePatientByIdFromLocal(userProfilePatientEntity: UserProfilePatientEntity) {
        viewModelScope.launch {
            chattingRepository.updateUserProfilePatientByIdFromLocal(userProfilePatientEntity)
        }
    }

    fun updateUserProfilePatientByIdFromApi(
        userPatientId: Int, namaBumil: String, nikBumil: String, tglLahirBumil: String,
        umurBumil: String, alamat: String, namaAyah: String, namaAnak: String, nikAnak: String,
        jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String, namaCabang: String
    ): LiveData<ResultState<UpdateUserProfilePatientByIdResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(
            chattingRepository.updateUserProfilePatientByIdFromApi(
                userPatientId, namaBumil, nikBumil, tglLahirBumil, umurBumil, alamat, namaAyah,
                namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak, namaCabang
            )
        )
    }

    fun getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId: Int) =
        chattingRepository.getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId)

    private fun getUserProfilePatientsFromApi() {
        viewModelScope.launch {
            _getUserProfilePatientsResult.value = ResultState.Loading
            _getUserProfilePatientsResult.value = chattingRepository.getUserProfilePatientsFromApi()
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}