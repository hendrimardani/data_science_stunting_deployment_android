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
import com.example.stunting.database.with_api.entities.user_profile.UserProfileWithUserRelation
import com.example.stunting.database.with_api.entities.users.UsersEntity
import com.example.stunting.database.with_api.response.AddingMessageResponse
import com.example.stunting.database.with_api.response.AddingUserGroupResponse
import com.example.stunting.database.with_api.response.GetAllMessagesResponse
import com.example.stunting.database.with_api.response.LoginResponse
import com.example.stunting.database.with_api.response.RegisterResponse
import com.example.stunting.database.with_api.response.UpdateUserProfileByIdResponse
import com.example.stunting.utils.AppExecutors
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ChattingRepository(
    private val chattingDatabase: ChattingDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val appExecutors: AppExecutors
) {
    var requestImageProfileFile: RequestBody? = null
    var multipartBodyImageProfile: MultipartBody.Part? = null
    var requestImageBannerFile: RequestBody? = null
    var multipartBodyImageBanner: MultipartBody.Part? = null

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
                                        nik = userProfile.nik,
                                        jenisKelamin = userProfile.jenisKelamin,
                                        tglLahir = userProfile.tglLahir,
                                        umur = userProfile.umur,
                                        alamat = userProfile.alamat,
                                        gambarProfile = userProfile.gambarProfile,
                                        gambarBanner = userProfile.gambarBanner,
                                        createdAt = userProfile.createdAt,
                                        updatedAt = userProfile.updatedAt
                                    )
                                )

                                groupsList.add(
                                    GroupsEntity(
                                        id = groups.id,
                                        namaGroup = groups.namaGroup,
                                        deskripsi = groups.deskripsi,
                                        gambarProfile = groups.gambarProfile,
                                        gambarBanner = groups.gambarBanner,
                                        createdAt = groups.createdAt,
                                        updatedAt = groups.updatedAt
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

    fun getUserGroupRelationByUserIdGroupId(userId: Int, groupId: Int): LiveData<List<UserGroupRelation>> {
        return chattingDatabase.userGroupDao().getUserGroupRelationByUserIdGroupId(userId, groupId)
    }

    fun getUserGroupRelationByGroupId(groupId: Int): LiveData<List<UserGroupRelation>> {
        return chattingDatabase.userGroupDao().getUserGroupRelationByGroupId(groupId)
    }

    fun getUserGroupRelationByUserId(userId: Int): LiveData<List<UserGroupRelation>> {
        return chattingDatabase.userGroupDao().getUserGroupRelationByUserId(userId)
    }

    // Menggunakan entitas pusat relasi
    fun getUserGroups(): LiveData<ResultState<List<UserGroupEntity>>> {
        resultListUserGroup.value = ResultState.Loading

        val response = apiService.getAllUserGroup()
        response.enqueue(object : Callback<GetAllUserGroupResponse> {
            override fun onResponse(
                call: Call<GetAllUserGroupResponse>,
                response: Response<GetAllUserGroupResponse>
            ) {
                if (response.isSuccessful) {
                    val userGroups = response.body()?.userGroups
                    val groupList = ArrayList<GroupsEntity>()
                    val userProfileList = ArrayList<UserProfileEntity>()
                    val userGroupList = ArrayList<UserGroupEntity>()

                    appExecutors.diskIO.execute {
                        userGroups?.forEach { item ->
                            val userProfile = item?.userProfile
                            val groups = item?.groups

                            if (groups != null && userProfile != null) {
                                userProfileList.add(
                                    UserProfileEntity(
                                        id = userProfile.id,
                                        userId = userProfile.userId,
                                        nama = userProfile.nama,
                                        nik = userProfile.nik,
                                        jenisKelamin = userProfile.jenisKelamin,
                                        tglLahir = userProfile.tglLahir,
                                        umur = userProfile.umur,
                                        alamat = userProfile.alamat,
                                        gambarProfile = userProfile.gambarProfile,
                                        gambarBanner = userProfile.gambarBanner,
                                        createdAt = userProfile.createdAt,
                                        updatedAt = userProfile.updatedAt
                                    )
                                )
                                groupList.add(
                                    GroupsEntity(
                                        id = groups.id,
                                        namaGroup = groups.namaGroup,
                                        deskripsi = groups.deskripsi,
                                        gambarProfile = groups.gambarProfile,
                                        gambarBanner = groups.gambarBanner,
                                        createdAt = groups.createdAt,
                                        updatedAt = groups.updatedAt
                                    )
                                )
                                userGroupList.add(
                                    UserGroupEntity(
                                        group_id = groups.id ?: 0,
                                        user_id = userProfile.userId ?: 0,
                                        role = item.role,
                                        createdBy = item.createdBy,
                                        createdAt = item.createdAt,
                                        updatedAt = item.updatedAt
                                    )
                                )
                            }
                        }

                        // Simpan ke Room
                        chattingDatabase.groupsDao().insertGroups(groupList)
                        chattingDatabase.userProfileDao().insertUserProfile(userProfileList)
                        chattingDatabase.userGroupDao().insertUserGroups(userGroupList)

                        // Trigger ambil ulang data dari Room setelah simpan selesai
                        val updatedData = chattingDatabase.userGroupDao().getUserGroup()
                        resultListUserGroup.postValue(ResultState.Success(updatedData))
                    }
                } else {
                    resultListUserGroup.postValue(ResultState.Error("Gagal mengambil data"))
                }
            }

            override fun onFailure(call: Call<GetAllUserGroupResponse>, t: Throwable) {
                resultListUserGroup.postValue(ResultState.Error(t.message ?: "Unknown error"))
            }
        })

        return resultListUserGroup
    }


    suspend fun addUserGroup(
        userId: List<Int>, namaGroup: String, deskripsi: String,
        gambarProfile: File?, gambarBanner: File?
    ): ResultState<AddingUserGroupResponse?> {
        return try {
            val requestBodyJson = AddingUserGroupRequestJSON(namaGroup, deskripsi)

            // Convert dataJson menjadi JSON String
            val gson = GsonBuilder()
                .serializeNulls() // Boleh null
                .create()
            val jsonData = gson.toJson(requestBodyJson)

            // Convert JSON string to RequestBody
            val dataRequestBody = jsonData.toRequestBody("text/plain".toMediaTypeOrNull())

            if (gambarProfile != null) {
                requestImageProfileFile = gambarProfile.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBodyImageProfile = MultipartBody.Part.createFormData(
                    "gambar_profile",
                    gambarProfile.name,
                    requestImageProfileFile!!
                )
            }

            if (gambarBanner != null) {
                requestImageBannerFile = gambarBanner.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBodyImageBanner = MultipartBody.Part.createFormData(
                    "gambar_banner",
                    gambarBanner.name,
                    requestImageBannerFile!!
                )
            }
            val response = apiService.addUserGroup(
                userId, dataRequestBody, multipartBodyImageProfile, multipartBodyImageBanner
            )

            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "onChattingRepository addUserGroup Error ${response.code()}: $errorBody")
                    ResultState.Error("Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            ResultState.Error("Exception: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
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
        userId: Int, nama: String?, nik: String?, umur: String?, alamat: String?, jenisKelamin: String?,
        tglLahir: String?, gambarProfile: File?, gambarBanner: File?
    ): ResultState<UpdateUserProfileByIdResponse?> {
        return try {
            val requestBodyJson = UpdateUserProfileByIdRequestJSON(nama, nik, umur, alamat, jenisKelamin, tglLahir)

            // Convert dataJson menjadi JSON String
            val gson = GsonBuilder()
                .serializeNulls() // Boleh null
                .create()
            val jsonData = gson.toJson(requestBodyJson)

            // Convert JSON string to RequestBody
            val dataRequestBody = jsonData.toRequestBody("text/plain".toMediaTypeOrNull())

            if (gambarProfile != null) {
                requestImageProfileFile = gambarProfile.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBodyImageProfile = MultipartBody.Part.createFormData(
                    "gambar_profile",
                    gambarProfile.name,
                    requestImageProfileFile!!
                )
            }

            if (gambarBanner != null) {
                requestImageBannerFile = gambarBanner.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBodyImageBanner = MultipartBody.Part.createFormData(
                    "gambar_banner",
                    gambarBanner.name,
                    requestImageBannerFile!!
                )
            }
            val response = apiService.updateUserProfileById(
                userId, dataRequestBody, multipartBodyImageProfile, multipartBodyImageBanner
            )

            if (response.isSuccessful) {
//                Log.d(TAG, "onChattingRepository updateUserProfileById() Success ${response.code()}: $response")
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository updateUserProfileById() Error ${response.code()}: $errorBody")
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

    suspend fun logout() {
        try {
            userPreference.logout()
        } catch (e: HttpException) {
//            Log.e(TAG, e.message())
        }
    }

    fun getUserProfileWithUserById(userId: Int): LiveData<UserProfileWithUserRelation> {
        return chattingDatabase.userProfileDao().getUserProfileWithUserById(userId)
    }

    fun getUserProfilesFromDatabase(): LiveData<List<UserProfileWithUserRelation>> {
        return chattingDatabase.userProfileDao().getUserProfilesFromDatabase()
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
                                        alamat = item.alamat,
                                        gambarProfile = item.gambarProfile,
                                        gambarBanner = item.gambarBanner,
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