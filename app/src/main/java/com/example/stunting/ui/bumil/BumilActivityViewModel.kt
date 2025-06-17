package com.example.stunting.ui.bumil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.AddingPregnantMomServiceResponse
import com.example.stunting.database.with_api.response.DataUserProfilePatientsItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class BumilActivityViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // UserProfilePatient
    private var _getUserProfilePatientFromApiResult = MutableLiveData<ResultState<List<DataUserProfilePatientsItem?>>>()
    val getUserProfilePatientFromApiResult = _getUserProfilePatientFromApiResult

    init {
        getUserProfilePatientsFromApi()
    }

    fun getUserProfilePatientByNamaBumil(namaBumil: String) =
        chattingRepository.getUserProfilePatientByNamaBumil(namaBumil)

    fun addPregnantMomServiceByUserId(
        userId: Int, categoryServiceId: Int, catatan: String?, namaBumil: String, hariPertamaHaidTerakhir: String,
        tglPerkiraanLahir: String, umurKehamilan: String, statusGiziKesehatan: String
    ): LiveData<ResultState<AddingPregnantMomServiceResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(
            chattingRepository.addPregnantMomServiceByUserId(
                userId, categoryServiceId, catatan, namaBumil, hariPertamaHaidTerakhir,
                tglPerkiraanLahir, umurKehamilan, statusGiziKesehatan
            )
        )
    }

    fun getUserProfilePatientsFromLocal() = chattingRepository.getUserProfilePatientsFromLocal()

    fun getUserProfilePatientsFromApi() {
        viewModelScope.launch {
            _getUserProfilePatientFromApiResult.value = ResultState.Loading
            _getUserProfilePatientFromApiResult.value = chattingRepository.getUserProfilePatientsFromApi()
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}