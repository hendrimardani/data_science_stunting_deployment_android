package com.example.stunting.datastore.chatting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.ChattingDatabase
import com.example.stunting.database.with_api.groups.GroupsEntity
import com.example.stunting.database.with_api.request_json.AddingMessageRequestJSON
import com.example.stunting.database.with_api.request_json.AddingUserGroupRequestJSON
import com.example.stunting.database.with_api.request_json.LoginRequestJSON
import com.example.stunting.database.with_api.request_json.RegisterRequestJSON
import com.example.stunting.database.with_api.request_json.UpdateUserProfileByIdRequestJSON
import com.example.stunting.database.with_api.response.GetAllUserGroupResponse
import com.example.stunting.database.with_api.response.GetAllUsersResponse
import com.example.stunting.database.with_api.retrofit.ApiService
import com.example.stunting.database.with_api.user_group.UserGroupEntity
import com.example.stunting.database.with_api.user_group.UserGroupRelation
import com.example.stunting.database.with_api.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.user_profile.UserWithUserProfile
import com.example.stunting.database.with_api.users.UsersEntity
import com.example.stunting.functions_helper.Functions.getDateTimePrimaryKey
import com.example.stunting.utils.AppExecutors
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChattingRepository(
    private val chattingDatabase: ChattingDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val appExecutors: AppExecutors
) {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDateTime = now.format(formatter)

    private val resultListUserGroup = MediatorLiveData<ResultState<List<UserGroupEntity>>>()
    private val resultListUsers = MediatorLiveData<ResultState<List<UserProfileEntity>>>()

    // Langsung memanggol ke API
    fun getMessageByGroupId(groupId: Int) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getMessageByGroupId(groupId)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
                Log.e(TAG, "onChattingRepository getMessageByGroupId Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
        }
    }

    fun addMessage(userId: Int, groupId: Int, isiPesan: String) = liveData {
        emit(ResultState.Loading)
        try {
            val requestBody = AddingMessageRequestJSON(isiPesan)
            val response = apiService.addMessage(userId, groupId, requestBody)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
                Log.e(TAG, "onChattingRepository addMessage Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
            emit(ResultState.Error(e.message.toString()))
            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
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
                            val group = item?.groups
                            val userProfile = item?.userProfile

                            if (group != null && userProfile != null) {
                                // Simpan Group
                                groupList.add(
                                    GroupsEntity(
                                        id = group.id,
                                        namaGroup = group.namaGroup,
                                        deskripsi = group.deskripsi,
                                        createdAt = formattedDateTime.toString(),
                                        updatedAt = formattedDateTime.toString()
                                    )
                                )

                                // Simpan UserProfile
                                userProfileList.add(
                                    UserProfileEntity(
                                        id = userProfile.id ?: 0,
                                        userId = userProfile.userId ?: 0,
                                        nama = userProfile.nama,
                                        nik = userProfile.nik,
                                        jenisKelamin = userProfile.jenisKelamin,
                                        tglLahir = userProfile.tglLahir,
                                        umur = userProfile.umur ?: 0,
                                        createdAt = formattedDateTime.toString(),
                                        updatedAt = formattedDateTime.toString()
                                    )
                                )

                                // Simpan UserGroup
                                userGroupList.add(
                                    UserGroupEntity(
                                        id_group = group.id ?: 0,
                                        user_id = userProfile.userId ?: 0,
                                        role = null,
                                        createdAt = formattedDateTime.toString(),
                                        updatedAt = formattedDateTime.toString()
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

    // Langsung memanggol ke API
    fun getUserGroupByUserId(userId: Int) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getUserGroupByUserId(userId)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
//                Log.e(TAG, "onChattingRepository getUserGroupByUserId Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: Exception) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            emit(ResultState.Error(e.message.toString()))
        }
    }


    suspend fun addUserGroup(userProfileId: Int, namaGroup: String, deskripsi: String) = liveData {
        emit(ResultState.Loading)
        try {
            val requestBody = AddingUserGroupRequestJSON(namaGroup, deskripsi)
            val response = apiService.addUserGroup(userProfileId, requestBody)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
//                Log.e(TAG, "onChattingRepository addUserGroup Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: Exception) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            emit(ResultState.Error(e.message.toString()))
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
                                        createdAt = formattedDateTime.toString(),
                                        updatedAt = formattedDateTime.toString()
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
                                        createdAt = formattedDateTime.toString(),
                                        updatedAt = formattedDateTime.toString(),
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

    suspend  fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val requestBody = LoginRequestJSON(email, password)
            val response = apiService.login(requestBody)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
//                Log.e(TAG, "onChattingRepository login Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
            emit(ResultState.Error(e.message.toString()))
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
        }
    }

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun register(nama: String, email: String, password: String, repeatPassword: String) = liveData {
        emit(ResultState.Loading)
        try {
            val requestBody = RegisterRequestJSON(nama, email, password, repeatPassword)
            val response = apiService.register(requestBody)

            if (response.isSuccessful) {
                emit(ResultState.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(ResultState.Error("Error ${response.code()}: $errorBody"))
//                Log.e(TAG, "onChattingRepository register Error ${response.code()}: $errorBody")
                }
            }
        } catch (e: HttpException) {
            emit(ResultState.Error(e.message.toString()))
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
        }
    }

    companion object {
        private val TAG = ChattingRepository::class.java.simpleName
    }
}