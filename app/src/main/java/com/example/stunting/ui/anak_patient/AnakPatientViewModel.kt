package com.example.stunting.ui.anak_patient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class AnakPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // Checks
    private var _getChecksFromApiResult = MutableLiveData<ResultState<List<DataChecksItem?>>>()
    val getChecksFromApiResult = _getChecksFromApiResult

    init {
        getChecksFromApi()
    }

    fun getTransactionCountByMonth() = chattingRepository.getTransactionCountByMonth()

    fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearch(userPatientId: Int, categoryServiceId: Int, searchQuery: String) =
        chattingRepository.getChecksRelationByUserPatientIdCategoryServiceIdWithSearch(userPatientId, categoryServiceId, searchQuery)

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