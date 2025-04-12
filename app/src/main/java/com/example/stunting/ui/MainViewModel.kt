package com.example.stunting.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.datastore.chatting.ChattingRepository
import com.example.stunting.datastore.chatting.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val chattingRepository: ChattingRepository): ViewModel() {

//    fun getMessageByGroupId(groupId: Int) = liveData {
//        emitSource(chattingRepository.getMessageByGroupId(groupId))
//    }

    fun addMessage(userId: Int, groupId: Int, isiPesan: String) = liveData {
        emitSource(chattingRepository.addMessage(userId, groupId, isiPesan))
    }

    fun getUserGroupRelationByUserId(userId: Int) = chattingRepository.getUserGroupRelationByUserId(userId)

    fun getUserGroup() = chattingRepository.getUserGroup()

    fun addUserGroup(userProfileById: Int, namaGroup: String, deskripsi: String) = liveData {
        emitSource(chattingRepository.addUserGroup(userProfileById, namaGroup, deskripsi))
    }

    fun deleteUserById(id: Int) = liveData {
        emitSource(chattingRepository.deleteUserById(id))
    }

    fun updateUserProfileById(
        userProfileById: Int,  nama: String, nik: String, umur: Int, jenis_kelamin: String, tgl_lahir: String
    ) = liveData {
        emitSource(
            chattingRepository.updateUserProfileById(userProfileById, nama, nik, umur, jenis_kelamin, tgl_lahir)
        )
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }

    fun getUserWithUserProfileById(userId: Int) = chattingRepository.getUserWithUserProfileById(userId)

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            chattingRepository.saveSession(userModel)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return chattingRepository.getSession().asLiveData()
    }

    fun getUsers() = chattingRepository.getUsers()

    fun login(email: String, password: String) = liveData {
        emitSource(chattingRepository.login(email, password))
    }

    fun register(nama: String, email: String, password: String, repeatPassword: String) = liveData {
        emitSource(chattingRepository.register(nama, email, password, repeatPassword))
    }

}