package com.example.stunting.ui.bumil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.checks.ChecksEntity
import com.example.stunting.database.with_api.entities.pregnant_mom_service.PregnantMomServiceEntity
import com.example.stunting.database.with_api.response.AddingPregnantMomServiceByUserIdResponse
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.database.with_api.response.DataPregnantMomServicesItem
import com.example.stunting.database.with_api.response.DataUserProfilePatientsItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class BumilViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // PregnantMomService
    private var _getPregnantMomServiceFromApiResult = MutableLiveData<ResultState<List<DataPregnantMomServicesItem?>>>()
    // Checks
    private var _getChecksFromApiResult = MutableLiveData<ResultState<List<DataChecksItem?>>>()
    val getChecksFromApiResult = _getChecksFromApiResult
    // UserProfilePatient
    private var _getUserProfilePatientFromApiResult = MutableLiveData<ResultState<List<DataUserProfilePatientsItem?>>>()
    val getUserProfilePatientFromApiResult = _getUserProfilePatientFromApiResult

    init {
        getChecksFromApi()
        getUserProfilePatientsFromApi()
    }

    fun getPregnantMomServiceFromApi() {
        viewModelScope.launch {
            _getPregnantMomServiceFromApiResult.value = ResultState.Loading
            _getPregnantMomServiceFromApiResult.value = chattingRepository.getPregnantMomServiceFromApi()
        }
    }

    suspend fun insertPregnantMomServices(pregnantMomServiceList: List<PregnantMomServiceEntity>) =
        chattingRepository.insertPregnantMomServices(pregnantMomServiceList)

    fun getChecksRelationByUserIdCategoryServiceIdPregnantMomService(userId: Int, categoryServiceId: Int) =
        chattingRepository.getChecksRelationByUserIdCategoryServiceIdPregnantMomService(userId, categoryServiceId)

    suspend fun insertChecks(checksList: List<ChecksEntity>) =
        chattingRepository.insertChecks(checksList)

    private fun getChecksFromApi() {
        viewModelScope.launch {
            _getChecksFromApiResult.value = ResultState.Loading
            _getChecksFromApiResult.value = chattingRepository.getChecksFromApi()
        }
    }

    fun getUserProfilePatientByNamaBumil(namaBumil: String) =
        chattingRepository.getUserProfilePatientByNamaBumil(namaBumil)

    fun addPregnantMomServiceByUserId(
        userId: Int, categoryServiceId: Int, catatan: String?, namaBumil: String, hariPertamaHaidTerakhir: String,
        tglPerkiraanLahir: String, umurKehamilan: String, statusGiziKesehatan: String
    ): LiveData<ResultState<AddingPregnantMomServiceByUserIdResponse?>> = liveData {
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