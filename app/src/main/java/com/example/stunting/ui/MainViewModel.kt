package com.example.stunting.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.AddingMessageResponse
import com.example.stunting.database.with_api.response.AddingUserGroupResponse
import com.example.stunting.database.with_api.response.LoginResponse
import com.example.stunting.database.with_api.response.RegisterResponse
import com.example.stunting.database.with_api.response.UpdateUserProfileByIdResponse
import com.example.stunting.datastore.chatting.ChattingRepository
import com.example.stunting.datastore.chatting.UserModel
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val chattingRepository: ChattingRepository): ViewModel() {
    fun getMessageRelationByGroupId(groupId: Int) = chattingRepository.getMessageRelationByGroupId(groupId)

    fun getMessages() = chattingRepository.getMessages()

    fun addMessage(userId: Int, groupId: Int, isiPesan: String) :
        LiveData<ResultState<AddingMessageResponse?>> = liveData {
            emit(ResultState.Loading)
            emit(chattingRepository.addMessage(userId, groupId, isiPesan))
        }

    fun getUserGroupRelationByUserId(userId: Int) = chattingRepository.getUserGroupRelationByUserId(userId)

    fun getUserGroup() = chattingRepository.getUserGroup()

    fun addUserGroup(userId: Int, namaGroup: String, deskripsi: String) :
        LiveData<ResultState<AddingUserGroupResponse?>> = liveData {
            emit(ResultState.Loading)
            emit(chattingRepository.addUserGroup(userId, namaGroup, deskripsi))
        }

    fun deleteUserById(id: Int) = liveData {
        emitSource(chattingRepository.deleteUserById(id))
    }

    fun updateUserProfileById(
        userId: Int, nama: String?, nik: String?, umur: String?, alamat: String?, jenisKelamin: String?,
        tglLahir: String?, gambarProfile: File?, gambarBanner: File?
    ): LiveData<ResultState<UpdateUserProfileByIdResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(
            chattingRepository.updateUserProfileById(
                userId, nama, nik, umur, alamat, jenisKelamin, tglLahir, gambarProfile, gambarBanner
            )
        )
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }

    fun getUserProfileWithUserById(userId: Int) = chattingRepository.getUserProfileWithUserById(userId)

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            chattingRepository.saveSession(userModel)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return chattingRepository.getSession().asLiveData()
    }

    fun deleteUsers() {
        viewModelScope.launch {
            chattingRepository.deleteUsers()
        }
    }

    fun getUsers() = chattingRepository.getUsers()

    fun login(email: String, password: String): LiveData<ResultState<LoginResponse?>> = liveData {
        emit(ResultState.Loading)
        emit(chattingRepository.login(email, password))
    }

    fun register(nama: String, email: String, password: String, repeatPassword: String) :
        LiveData<ResultState<RegisterResponse?>> = liveData {
            emit(ResultState.Loading)
            emit(chattingRepository.register(nama, email, password, repeatPassword))
        }
}