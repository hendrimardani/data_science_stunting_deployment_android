package com.example.stunting.ui.nav_drawer_patient_fragment.daftar_anak

import androidx.lifecycle.ViewModel
import com.example.stunting.datastore.chatting.ChattingRepository

class NavDaftarAnakPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    fun getChildrenPatientByUserPatientIdFromLocal(userPatientId: Int) =
        chattingRepository.getChildrenPatientByUserPatientIdFromLocal(userPatientId)
}