package com.example.stunting.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.LoginResponse
import com.example.stunting.datastore.chatting.ChattingRepository
import com.example.stunting.datastore.chatting.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val chattingRepository: ChattingRepository): ViewModel() {

    fun login(email: String, password: String): LiveData<ResultState<LoginResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(chattingRepository.login(email, password))
    }

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            chattingRepository.saveSession(userModel)
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}