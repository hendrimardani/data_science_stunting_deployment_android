package com.example.stunting.ui.anak_patient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.database.with_api.response.DataChildServicesItem
import com.example.stunting.database.with_api.response.DataPregnantMomServicesItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class AnakPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // ChildService
    private var _getChildServiceFromApiResult = MutableLiveData<ResultState<List<DataChildServicesItem?>>>()

    // Checks
    private var _getChecksFromApiResult = MutableLiveData<ResultState<List<DataChecksItem?>>>()
    val getChecksFromApiResult = _getChecksFromApiResult

    init {
        getChecksFromApi()
    }

    fun getChildServiceFromApi() {
        viewModelScope.launch {
            _getChildServiceFromApiResult.value = ResultState.Loading
            _getChildServiceFromApiResult.value = chattingRepository.getChildServiceFromApi()
        }
    }

    fun getTransactionCountByMonth() = chattingRepository.getTransactionCountByMonth()

    fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearchAnak(userPatientId: Int, categoryServiceId: Int, searchQuery: String) =
        chattingRepository.getChecksRelationByUserPatientIdCategoryServiceIdWithSearchAnak(userPatientId, categoryServiceId, searchQuery)

    private fun getChecksFromApi() {
        viewModelScope.launch {
            _getChecksFromApiResult.value = ResultState.Loading
            _getChecksFromApiResult.value = chattingRepository.getChecksFromApi()
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}