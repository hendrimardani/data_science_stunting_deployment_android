package com.example.stunting.ui.nav_drawer_patient_fragment.daftar_anak

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataChildrenPatientByUserPatientIdItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class NavDaftarAnakPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    // ChildrenPatient
    private var _getChildrenPatientByUserPatientIdFromApiResult = MutableLiveData<ResultState<List<DataChildrenPatientByUserPatientIdItem?>>>()
    val getChildrenPatientByUserPatientIdFromApiResult = _getChildrenPatientByUserPatientIdFromApiResult

    fun getChildrenPatientByUserPatientIdFromLocal(userPatientId: Int) =
        chattingRepository.getChildrenPatientByUserPatientIdFromLocal(userPatientId)

    fun getChildrenPatientByUserPatientIdFromApi(userPatientId: Int) {
        viewModelScope.launch {
            _getChildrenPatientByUserPatientIdFromApiResult.value = ResultState.Loading
            _getChildrenPatientByUserPatientIdFromApiResult.value = chattingRepository.getChildrenPatientByUserPatientIdFromApi(userPatientId)
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}