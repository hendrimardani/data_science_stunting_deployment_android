package com.example.stunting.ui.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataBranchesItem
import com.example.stunting.database.with_api.response.RegisterResponse
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val chattingRepository: ChattingRepository): ViewModel() {

    private var _getBranchesResult = MutableLiveData<ResultState<List<DataBranchesItem?>>>()
    val getBranchesResult = _getBranchesResult

    init {
        getBranchesFromApi()
    }

    fun getBranchesFromLocal() = chattingRepository.getBranchesFromLocal()

    private fun getBranchesFromApi() {
        viewModelScope.launch {
            _getBranchesResult.value = ResultState.Loading
            _getBranchesResult.value = chattingRepository.getBranchesFromApi()
        }
    }

    fun register(
        nama: String, email: String, namaCabang: String, password: String, repeatPassword: String
    ): LiveData<ResultState<RegisterResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(chattingRepository.register(nama, email, namaCabang, password, repeatPassword))
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}