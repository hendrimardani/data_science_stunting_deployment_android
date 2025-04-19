package com.example.stunting.datastore.chatting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.ChattingDatabase
import com.example.stunting.database.with_api.entities.groups.GroupsEntity
import com.example.stunting.database.with_api.entities.messages.MessagesEntity
import com.example.stunting.database.with_api.entities.messages.MessagesRelation
import com.example.stunting.database.with_api.entities.notifications.NotificationsEntity
import com.example.stunting.database.with_api.request_json.AddingMessageRequestJSON
import com.example.stunting.database.with_api.request_json.AddingUserGroupRequestJSON
import com.example.stunting.database.with_api.request_json.LoginRequestJSON
import com.example.stunting.database.with_api.request_json.RegisterRequestJSON
import com.example.stunting.database.with_api.request_json.UpdateUserProfileByIdRequestJSON
import com.example.stunting.database.with_api.response.GetAllUserGroupResponse
import com.example.stunting.database.with_api.response.GetAllUsersResponse
import com.example.stunting.database.with_api.retrofit.ApiService
import com.example.stunting.database.with_api.entities.user_group.UserGroupEntity
import com.example.stunting.database.with_api.entities.user_group.UserGroupRelation
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.entities.user_profile.UserWithUserProfile
import com.example.stunting.database.with_api.entities.users.UsersEntity
import com.example.stunting.database.with_api.response.AddingMessageResponse
import com.example.stunting.database.with_api.response.AddingUserGroupResponse
import com.example.stunting.database.with_api.response.GetAllMessagesResponse
import com.example.stunting.database.with_api.response.LoginResponse
import com.example.stunting.database.with_api.response.RegisterResponse
import com.example.stunting.functions_helper.Functions.getDateTimePrimaryKey
import com.example.stunting.utils.AppExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChattingRepository(
    private val chattingDatabase: ChattingDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val appExecutors: AppExecutors
) {
    private val resultListUsers = MediatorLiveData<ResultState<List<UserProfileEntity>>>()
    private val resultListMessages = MediatorLiveData<ResultState<List<MessagesEntity>>>()
    private val resultListUserGroup = MediatorLiveData<ResultState<List<UserGroupEntity>>>()

    fun getMessageRelationByGroupId(groupId: Int): LiveData<List<MessagesRelation>> {
        return chattingDatabase.messagesDao().getMessageRelationByGroupId(groupId)
    }

    // Menggunakan entitas pusat relasi
    fun getMessages(): LiveData<ResultState<List<MessagesEntity>>> {
        resultListMessages.value = ResultState.Loading
        val response = apiService.getAllMessages()
        response.enqueue(object : Callback<GetAllMessagesResponse> {
            override fun onResponse(
                call: Call<GetAllMessagesResponse>,
                response: Response<GetAllMessagesResponse>
            ) {
                if (response.isSuccessful) {
                    // hasil: 2025-04-18T14:00:00+07:00
                    val currentDateTime = OffsetDateTime.now(ZoneOffset.of("+07:00"))
                    val dateTimeFormatted = currentDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

                    val messages = response.body()?.dataMessages
//                    Log.d(TAG, "onChattingRepository getMessages success: $messages")

                    val userProfileList = ArrayList<UserProfileEntity>()
                    val groupsList = ArrayList<GroupsEntity>()
                    val notificationsList = ArrayList<NotificationsEntity>()
                    val messagesList = ArrayList<MessagesEntity>()

                    appExecutors.diskIO.execute {
                        messages?.forEach { item ->
                            val userProfile = item?.userProfile
                            val groups = item?.groups
                            val notifications = item?.notifications

                            if (userProfile != null && groups != null && notifications != null) {
                                userProfileList.add(
                                    UserProfileEntity(
                                        id = userProfile.id,
                                        userId = userProfile.userId,
                                        nama = userProfile.nama,
                                        jenisKelamin = userProfile.jenisKelamin,
                                        createdAt = dateTimeFormatted,
                                        updatedAt = dateTimeFormatted
                                    )
                                )

                                groupsList.add(
                                    GroupsEntity(
                                        id = groups.id,
                                        namaGroup = groups.namaGroup,
                                        deskripsi = groups.deskripsi,
                                        createdAt = dateTimeFormatted,
                                        updatedAt = dateTimeFormatted
                                    )
                                )

                                notificationsList.add(
                                    NotificationsEntity(
                                        id = notifications.id,
                                        isStatus = notifications.isStatus,
                                        createdAt = dateTimeFormatted,
                                        updatedAt = dateTimeFormatted
                                    )
                                )

                                messagesList.add(
                                    MessagesEntity(
                                        user_id = userProfile.userId ?: 0,
                                        group_id = groups.id ?: 0,
                                        notification_id = notifications.id ?: 0,
                                        isi_pesan = item.isiPesan,
                                        createdAt = dateTimeFormatted,
                                        updatedAt = dateTimeFormatted
                                    )
                                )
                            }
                            chattingDatabase.userProfileDao().insertUserProfile(userProfileList)
                            chattingDatabase.groupsDao().insertGroups(groupsList)
                            chattingDatabase.notificationsDao().insertNotifications(notificationsList)
                            chattingDatabase.messagesDao().insertMessages(messagesList)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetAllMessagesResponse>, t: Throwable) {
//                Log.d(TAG, "onChattingRepository getMessages Failed : ${t.message}")
                resultListMessages.value = ResultState.Error(t.message.toString())
            }
        })
        val localData = chattingDatabase.messagesDao().getMessages()
        resultListMessages.addSource(localData) { userData: List<MessagesEntity> ->
            resultListMessages.value = ResultState.Success(userData)
        }
        return resultListMessages
    }

    suspend fun addMessage(userId: Int, groupId: Int, isiPesan: String): ResultState<AddingMessageResponse?> {
        return try {
            val requestBody = AddingMessageRequestJSON(isiPesan)
            val response = apiService.addMessage(userId, groupId, requestBody)

            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository addMessage Error ${response.code()}: $errorBody")
                    ResultState.Error("Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            ResultState.Error("Exception: ${e.message}")
        } catch (e: Exception) {
//            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }


    fun getUserGroupRelationByUserId(userId: Int): LiveData<List<UserGroupRelation>> {
        return chattingDatabase.userGroupDao().getUserGroupRelationByUserId(userId)
    }

    // Menggunakan entitas pusat relasi
    fun getUserGroup(): LiveData<ResultState<List<UserGroupEntity>>> {
        resultListUserGroup.value = ResultState.Loading
        val response = apiService.getAllUserGroup()

        response.enqueue(object : Callback<GetAllUserGroupResponse> {
            override fun onResponse(
                call: Call<GetAllUserGroupResponse>,
                response: Response<GetAllUserGroupResponse>
            ) {
                if (response.isSuccessful) {
                    val userGroups = response.body()?.userGroups
//                    Log.d(TAG, "onChattingRepository getUserGroup Success : ${userGroups}")
                    val groupList = ArrayList<GroupsEntity>()
                    val userProfileList = ArrayList<UserProfileEntity>()
                    val userGroupList = ArrayList<UserGroupEntity>()

                    appExecutors.diskIO.execute {
                        userGroups?.forEach { item ->
                            val userProfile = item?.userProfile
                            val group = item?.groups

                            if (group != null && userProfile != null) {
                                // Simpan UserProfile
                                userProfileList.add(
                                    UserProfileEntity(
                                        id = userProfile.id,
                                        userId = userProfile.userId ,
                                        nama = userProfile.nama,
                                        nik = userProfile.nik,
                                        jenisKelamin = userProfile.jenisKelamin,
                                        tglLahir = userProfile.tglLahir,
                                        umur = userProfile.umur,
                                        createdAt = userProfile.createdAt,
                                        updatedAt = userProfile.updatedAt
                                    )
                                )

                                // Simpan Groups
                                groupList.add(
                                    GroupsEntity(
                                        id = group.id,
                                        namaGroup = group.namaGroup,
                                        deskripsi = group.deskripsi,
                                        createdAt = group.createdAt,
                                        updatedAt = group.updatedAt
                                    )
                                )

                                // Simpan UserGroup
                                userGroupList.add(
                                    UserGroupEntity(
                                        group_id = group.id ?: 0,
                                        user_id = userProfile.userId ?: 0,
                                        role = item.role,
                                        createdBy = item.createdBy,
                                        createdAt = item.createdAt,
                                        updatedAt = item.updatedAt
                                    )
                                )
                            }
                        }
                        chattingDatabase.groupsDao().insertGroups(groupList)
                        chattingDatabase.userProfileDao().insertUserProfile(userProfileList)
                        chattingDatabase.userGroupDao().insertUserGroups(userGroupList)
                    }
                }
            }

            override fun onFailure(call: Call<GetAllUserGroupResponse>, t: Throwable) {
//                Log.d(TAG, "onChattingRepository getUserGroup Failed : ${t.message}")
                resultListUserGroup.value = ResultState.Error(t.message.toString())
            }
        })
        val localData = chattingDatabase.userGroupDao().getUserGroup()
        resultListUserGroup.addSource(localData) { userData: List<UserGroupEntity> ->
            resultListUserGroup.value = ResultState.Success(userData)
        }
        return resultListUserGroup
    }

    suspend fun addUserGroup(userProfileId: Int, namaGroup: String, deskripsi: String): ResultState<AddingUserGroupResponse?> {
        return try {
            val requestBody = AddingUserGroupRequestJSON(namaGroup, deskripsi)
            val response = apiService.addUserGroup(userProfileId, requestBody)

            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository addUserGroup Error ${response.code()}: $errorBody")
                    ResultState.Error("Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            ResultState.Error("Exception: ${e.message}")
        } catch (e: Exception) {
//            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun deleteUserById(id: Int) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.deleteUserById(id)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
//                Log.e(TAG, "onChattingRepository deleteUserById Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            emit(ResultState.Error(e.message.toString()))
        }
    }

    suspend fun updateUserProfileById(
        userProfileId: Int, nama: String, nik: String, umur: Int, jenisKelamin: String, tglLahir: String
    ) = liveData {
        emit(ResultState.Loading)
        try {
            val requestBody = UpdateUserProfileByIdRequestJSON(nama, nik, umur, jenisKelamin, tglLahir)
            val response = apiService.updateUserProfileById(userProfileId, requestBody)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
//                Log.e(TAG, "onChattingRepository updateUserProfileById Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
            emit(ResultState.Error(e.message.toString()))
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
        }
    }

    suspend fun logout() {
        try {
            userPreference.logout()
        } catch (e: HttpException) {
//            Log.e(TAG, e.message())
        }
    }

    fun getUserWithUserProfileById(userId: Int): LiveData<UserWithUserProfile>{
        return chattingDatabase.userProfileDao().getUserWithUserProfileById(userId)
    }
    
    // Menggunakan entitas pusat relasi
    fun getUsers(): LiveData<ResultState<List<UserProfileEntity>>> {
        resultListUsers.value = ResultState.Loading
        val response = apiService.getAllUsers()
        response.enqueue(object: Callback<GetAllUsersResponse> {
            override fun onResponse(
                call: Call<GetAllUsersResponse>,
                response: Response<GetAllUsersResponse>
            ) {
//                Log.d(TAG, "onChattingRepository getUsers raw response : ${response.raw()}")
                if (response.isSuccessful) {
                    val users = response.body()?.dataUsers
//                    Log.d(TAG, "onChattingRepository getUsers Success : ${users}")
                    val usersList = ArrayList<UsersEntity>()
                    val userProfileList = ArrayList<UserProfileEntity>()

                    appExecutors.diskIO.execute {
                        users?.forEach { item ->
                            val users = item?.users
                            if (users != null) {
                                // Simpan Users
                                usersList.add(
                                    UsersEntity(
                                        id = item.users.id,
                                        email = item.users.email,
                                        password = item.users.password,
                                        createdAt = item.createdAt,
                                        updatedAt = item.updatedAt
                                    )
                                )

                                // Simpan UserProfile
                                userProfileList.add(
                                    UserProfileEntity(
                                        id = item.id,
                                        userId = item.userId,
                                        nama = item.nama,
                                        nik = item.nik,
                                        jenisKelamin = item.jenisKelamin,
                                        tglLahir = item.tglLahir,
                                        umur = item.umur,
                                        createdAt = item.createdAt,
                                        updatedAt = item.updatedAt,
                                    )
                                )
                            }
                        }
                        chattingDatabase.usersDao().insertUsers(usersList)
                        chattingDatabase.userProfileDao().insertUserProfile(userProfileList)
                    }
                }
            }

            override fun onFailure(call: Call<GetAllUsersResponse>, t: Throwable) {
//                Log.d(TAG, "onChattingRepository getUsers Failed : ${t.message}")
                resultListUsers.value = ResultState.Error(t.message.toString())
            }
        })
        val localData = chattingDatabase.userProfileDao().getUserProfiles()
        resultListUsers.addSource(localData) { userData: List<UserProfileEntity> ->
            resultListUsers.value = ResultState.Success(userData)
        }
        return resultListUsers
    }

    suspend fun login(email: String, password: String): ResultState<LoginResponse?> {
        return try {
            val requestBody = LoginRequestJSON(email, password)
            val response = apiService.login(requestBody)

            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository login Error ${response.code()}: $errorBody")
                    ResultState.Error("Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            ResultState.Error("Exception: ${e.message}")
        } catch (e: Exception) {
//            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun register(nama: String, email: String, password: String, repeatPassword: String): ResultState<RegisterResponse?> {
        return try {
            val requestBody = RegisterRequestJSON(nama, email, password, repeatPassword)
            val response = apiService.register(requestBody)

            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository register Error ${response.code()}: $errorBody")
                    ResultState.Error("Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            ResultState.Error("Exception: ${e.message}")
        } catch (e: Exception) {
//            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    companion object {
        private val TAG = ChattingRepository::class.java.simpleName
    }
}