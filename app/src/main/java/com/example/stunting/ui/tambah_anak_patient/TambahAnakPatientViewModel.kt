package com.example.stunting.ui.tambah_anak_patient

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.AddingChildrenPatientByUserPatientIdResponse
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class TambahAnakPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    fun addChildrenPatientByUserPatientId(
        userPatientId: Int, namaAnak: String, nikAnak: String,
        jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ): LiveData<ResultState<AddingChildrenPatientByUserPatientIdResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(
            chattingRepository.addChildrenPatientByUserPatientId(
                userPatientId, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak
            )
        )
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}