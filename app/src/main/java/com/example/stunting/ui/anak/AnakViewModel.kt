package com.example.stunting.ui.anak

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.checks.ChecksEntity
import com.example.stunting.database.with_api.entities.child_service.ChildServiceEntity
import com.example.stunting.database.with_api.response.AddingChildServiceByUserIdResponse
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.database.with_api.response.DataChildServicesItem
import com.example.stunting.database.with_api.response.DataChildrenPatientsItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class AnakViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // PregnantMomService
    private var _getChildServiceFromApiResult = MutableLiveData<ResultState<List<DataChildServicesItem?>>>()
    // Checks
    private var _getChecksFromApiResult = MutableLiveData<ResultState<List<DataChecksItem?>>>()
    val getChecksFromApiResult = _getChecksFromApiResult
    // ChildrenPatient
    private var _getChildrenPatientsFromApiResult = MutableLiveData<ResultState<List<DataChildrenPatientsItem?>>>()
    val getChildrenPatientsFromApiResult = _getChildrenPatientsFromApiResult

    init {
        getChecksFromApi()
        getChildrenPatientsFromApi()
    }

    fun getPregnantMomServiceFromApi() {
        viewModelScope.launch {
            _getChildServiceFromApiResult.value = ResultState.Loading
            _getChildServiceFromApiResult.value = chattingRepository.getChildServiceFromApi()
        }
    }

    suspend fun insertChildServices(insertChildServices: List<ChildServiceEntity>) =
        chattingRepository.insertChildServices(insertChildServices)

    suspend fun insertChecks(checksList: List<ChecksEntity>) =
        chattingRepository.insertChecks(checksList)

    fun getChecksRelationByUserIdCategoryServiceIdChildService(userId: Int, categoryServiceId: Int) =
        chattingRepository.getChecksRelationByUserIdCategoryServiceIdChildService(userId, categoryServiceId)

    private fun getChecksFromApi() {
        viewModelScope.launch {
            _getChecksFromApiResult.value = ResultState.Loading
            _getChecksFromApiResult.value = chattingRepository.getChecksFromApi()
        }
    }

    fun getChildrenPatientByNamaAnak(namaAnak: String) =
        chattingRepository.getChildrenPatientByNamaAnak(namaAnak)

    fun addChildServiceByUserId(
        userId: Int, categoryServiceId: Int, catatan: String?, namaAnak: String,
        tinggiCm: String, hasilPemeriksaan: String
    ): LiveData<ResultState<AddingChildServiceByUserIdResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(
            chattingRepository.addChildServiceByUserId(
                userId, categoryServiceId, catatan, namaAnak, tinggiCm, hasilPemeriksaan
            )
        )
    }

    fun getChildrenPatientsFromLocal() = chattingRepository.getChildrenPatientsFromLocal()

    fun getChildrenPatientsFromApi() {
        viewModelScope.launch {
            _getChildrenPatientsFromApiResult.value = ResultState.Loading
            _getChildrenPatientsFromApiResult.value = chattingRepository.getChildrenPatientsFromApi()
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}