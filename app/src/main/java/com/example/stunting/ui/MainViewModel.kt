package com.example.stunting.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.messages.MessagesEntity
import com.example.stunting.database.with_api.entities.user_group.UserGroupEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.response.AddingMessageResponse
import com.example.stunting.database.with_api.response.AddingUserByGroupIdResponse
import com.example.stunting.database.with_api.response.AddingUserGroupResponse
import com.example.stunting.database.with_api.response.DataMessagesItem
import com.example.stunting.database.with_api.response.DataUserProfilesItem
import com.example.stunting.database.with_api.response.UpdateGroupByIdResponse
import com.example.stunting.database.with_api.response.UpdateUserProfileByIdResponse
import com.example.stunting.database.with_api.response.UserGroupsItem
import com.example.stunting.datastore.chatting.ChattingRepository
import com.example.stunting.datastore.chatting.UserModel
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val chattingRepository: ChattingRepository): ViewModel() {

    private var _updateGroupByIdResult = MutableLiveData<ResultState<UpdateGroupByIdResponse?>>()
    val updateGroupByIdResult = _updateGroupByIdResult

    private var _updateUserProfileByIdResult = MutableLiveData<ResultState<UpdateUserProfileByIdResponse?>>()
    val updateUserProfileByIdResult = _updateUserProfileByIdResult

//    // Messages
//    private var _getMessagesResult = MutableLiveData<ResultState<List<DataMessagesItem?>>>()
//    val getMessagesResult = _getMessagesResult
//    val getMessagesFromLocal: LiveData<List<MessagesEntity>> = chattingRepository.getMessagesFromLocal()
//
//    // UserGroup
//    private var _getUserGroupsResult = MutableLiveData<ResultState<List<UserGroupsItem?>>>()
//    val getUserGroupsResult = _getUserGroupsResult
//    val getUserGroupsFromLocal: LiveData<List<UserGroupEntity>> = chattingRepository.getUserGroupsFromLocal()

    val getUserProfilesFromLocal: LiveData<List<UserProfileEntity>> = chattingRepository.getUserProfilesFromLocal()

//    // Connect to Realtime WebSocket
//    fun connect() {
//        chattingRepository.connect()
//    }
//
//    // Disconnect WebSocket
//    fun disconnect() {
//        chattingRepository.disconnect()
//    }
//
//    fun getMessageRelationByGroupId(groupId: Int) = chattingRepository.getMessageRelationByGroupId(groupId)
//
//    fun getMessagesFromApi() {
//        viewModelScope.launch {
//            _getMessagesResult.value = ResultState.Loading
//            _getMessagesResult.value = chattingRepository.getMessagesFromApi()
//        }
//    }
//
//    fun addMessage(
//        userId: Int, groupId: Int, isiPesan: String
//    ): LiveData<ResultState<AddingMessageResponse?>> = liveData {
//        emit(ResultState.Loading)
//        emit(chattingRepository.addMessage(userId, groupId, isiPesan))
//    }
//
//    fun getUserGroupRelationByUserIdRole(userId: Int, role: String?) =
//        chattingRepository.getUserGroupRelationByUserIdRole(userId, role)
//
//    fun getUserGroupRelationByUserIdGroupId(userId: Int, groupId: Int) =
//        chattingRepository.getUserGroupRelationByUserIdGroupId(userId, groupId)
//
//    fun getUserGroupRelationByGroupIdList(groupId: Int) = chattingRepository.getUserGroupRelationByGroupIdList(groupId)
//
//    fun getUserGroupRelationByGroupId(groupId: Int) = chattingRepository.getUserGroupRelationByGroupId(groupId)
//
//    fun getUserGroupRelationByUserId(userId: Int) = chattingRepository.getUserGroupRelationByUserId(userId)
//
//    fun updateGroupById(
//        groupId: Int, userId: Int, namaGroup: String?, deskripsi: String?, gambarProfile: File?, gambarBanner: File?
//    ) {
//        viewModelScope.launch {
//            _updateGroupByIdResult.value = ResultState.Loading
//            val result = chattingRepository.updateGroupById(
//                groupId, userId, namaGroup, deskripsi, gambarProfile, gambarBanner
//            )
//            _updateGroupByIdResult.value = result
//        }
//    }
//
//    fun getUserGroupsFromApi() {
//        viewModelScope.launch {
//            _getUserGroupsResult.value = ResultState.Loading
//            _getUserGroupsResult.value = chattingRepository.getUserGroupsFromApi()
//        }
//    }
//
//    fun addUserGroup(
//        userId: List<Int>, namaGroup: String, deskripsi: String, gambarProfile: File?, gambarBanner: File?
//    ): LiveData<ResultState<AddingUserGroupResponse?>> = liveData {
//        emit(ResultState.Loading)
//        emit(chattingRepository.addUserGroup(userId, namaGroup, deskripsi, gambarProfile, gambarBanner))
//    }
//
//    fun addUserByGroupId(
//        groupId: Int, userId: List<Int>, role: String?
//    ): LiveData<ResultState<AddingUserByGroupIdResponse?>> = liveData {
//        emit(ResultState.Loading)
//        emit(chattingRepository.addUserByGroupId(groupId, userId, role))
//    }

    fun deleteUserById(id: Int) = liveData {
        emitSource(chattingRepository.deleteUserById(id))
    }

    fun updateUserProfileById(
        userId: Int, nama: String?, nik: String?, umur: String?, alamat: String?, jenisKelamin: String?,
        tglLahir: String?, gambarProfile: File?, gambarBanner: File?
    ) {
        viewModelScope.launch {
            _updateUserProfileByIdResult.value = ResultState.Loading
            val result = chattingRepository.updateUserProfileById(
                userId, nama, nik, umur, alamat, jenisKelamin, tglLahir, gambarProfile, gambarBanner
            )
            _updateUserProfileByIdResult.value = result
        }
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
}