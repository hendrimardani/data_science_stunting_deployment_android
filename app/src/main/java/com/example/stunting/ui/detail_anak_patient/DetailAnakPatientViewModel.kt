package com.example.stunting.ui.detail_anak_patient

import androidx.lifecycle.ViewModel
import com.example.stunting.datastore.chatting.ChattingRepository

class DetailAnakPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {

    fun getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId: Int, userPatientId: Int) =
        chattingRepository.getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId, userPatientId)
}