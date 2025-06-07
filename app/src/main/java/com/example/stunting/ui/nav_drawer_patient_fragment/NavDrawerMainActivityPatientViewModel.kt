package com.example.stunting.ui.nav_drawer_patient_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class NavDrawerMainActivityPatientViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}