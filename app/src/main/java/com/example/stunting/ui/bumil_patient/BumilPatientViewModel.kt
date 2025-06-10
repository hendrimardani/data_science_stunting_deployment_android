package com.example.stunting.ui.bumil_patient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class BumilPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // Checks
    private var _getChecksFromApiResult = MutableLiveData<ResultState<List<DataChecksItem?>>>()
    val getChecksFromApiResult = _getChecksFromApiResult

    init {
        getChecksFromApi()
    }

    fun getChecksRelationByUserPatientIdCategoryServiceId(userPatientId: Int, categoryServiceId: Int) =
        chattingRepository.getChecksRelationByUserPatientIdCategoryServiceId(userPatientId, categoryServiceId)

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