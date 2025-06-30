package com.example.stunting.ui.detail_anak_patient

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.UpdateChildrenPatientByUserPatientIdResponse
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class DetailAnakPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    fun updateChildrenPatientByUserPatientIdFromLocal(
        userPatientId: Int, namaAnak: String, nikAnak: String, jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ) {
        viewModelScope.launch {
            chattingRepository.updateChildrenPatientByUserPatientIdFromLocal(userPatientId, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak)
        }
    }

    fun updateChildrenPatientByUserPatientIdFromApi(
        userPatientId: Int, namaAnak: String, nikAnak: String,
        jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ): LiveData<ResultState<UpdateChildrenPatientByUserPatientIdResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(
            chattingRepository.updateChildrenPatientByUserPatientIdFromApi(
                userPatientId, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak
            )
        )
    }

    fun getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId: Int, userPatientId: Int) =
        chattingRepository.getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId, userPatientId)

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}