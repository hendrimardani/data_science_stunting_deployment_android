package com.example.stunting.ui.bumil_patient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class BumilPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    private var _getChecksFromApi = MutableLiveData<ResultState<List<DataChecksItem?>>>()
    val getChecksFromApi = _getChecksFromApi

    init {
        getChecksFromApi()
    }

    fun getChecksRelationByChildrenPatientId(childrenPatientId: Int) =
        chattingRepository.getChecksRelationByChildrenPatientId(childrenPatientId)

    private fun getChecksFromApi() {
        viewModelScope.launch {
            _getChecksFromApi.value = ResultState.Loading
            _getChecksFromApi.value = chattingRepository.getChecksFromApi()
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}