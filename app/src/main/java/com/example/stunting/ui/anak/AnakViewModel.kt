package com.example.stunting.ui.anak

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.database.with_api.response.DataChildrenPatientsItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class AnakViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // ChildrenPatient
    private var _getChildrenPatientsFromApiResult = MutableLiveData<ResultState<List<DataChildrenPatientsItem?>>>()
    val getChildrenPatientsFromApiResult = _getChildrenPatientsFromApiResult
    // Checks
    private var _getChecksFromApiResult = MutableLiveData<ResultState<List<DataChecksItem?>>>()
    val getChecksFromApiResult = _getChecksFromApiResult

    init {
        getChecksFromApi()
        getChildrenPatientsFromApi()
    }

    private fun getChecksFromApi() {
        viewModelScope.launch {
            _getChecksFromApiResult.value = ResultState.Loading
            _getChecksFromApiResult.value = chattingRepository.getChecksFromApi()
        }
    }

    fun getChildrenPatientByNamaAnak(namaAnak: String) = chattingRepository.getChildrenPatientByNamaAnak(namaAnak)

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