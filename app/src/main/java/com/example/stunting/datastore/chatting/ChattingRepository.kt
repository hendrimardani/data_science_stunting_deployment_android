package com.example.stunting.datastore.chatting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.ChattingDatabase
import com.example.stunting.database.with_api.request_json.AddingUserGroupRequestJSON
import com.example.stunting.database.with_api.request_json.LoginRequestJSON
import com.example.stunting.database.with_api.request_json.RegisterRequestJSON
import com.example.stunting.database.with_api.request_json.UpdateUserProfileByIdRequestJSON
import com.example.stunting.database.with_api.response.GetAllUsersResponse
import com.example.stunting.database.with_api.retrofit.ApiService
import com.example.stunting.database.with_api.user_group.GroupWithUserProfiles
import com.example.stunting.database.with_api.user_group.UserGroupEntity
import com.example.stunting.database.with_api.user_group.UserProfileWithGroups
import com.example.stunting.database.with_api.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.user_profile.UserWithUserProfile
import com.example.stunting.database.with_api.users.UsersEntity
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

    private val resultListUsers = MediatorLiveData<ResultState<List<UserProfileEntity>>>()

    fun getUserProfilesWithGroupsByUserProfileId(userId: Int): LiveData<UserGroupEntity> {
        return chattingDatabase.userGroupDao().getUserProfilesWithGroupsByUserProfileId(userId)
    }

    fun getGroupWithUserProfiles(): LiveData<GroupWithUserProfiles> {
        return chattingDatabase.userGroupDao().getGroupWithUserProfiles()
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
                        // Simpan di Entitas Users
                        users?.forEach { item ->
                            val user = UsersEntity(
                                item?.users?.id,
                                item?.users?.email,
                                item?.users?.password,
                                formattedDateTime.toString(),
                                formattedDateTime.toString()
                            )
                            usersList.add(user)
                        }
                        chattingDatabase.usersDao().insertUsers(usersList)

                        // Simpan di Entitas User Profile
                        users!!.forEach { item ->
                            val userProfile = UserProfileEntity(
                                item?.id,
                                item?.userId,
                                item?.nama,
                                item?.nik,
                                item?.jenisKelamin,
                                item?.tglLahir,
                                item?.umur,
                                formattedDateTime.toString(),
                                formattedDateTime.toString(),
                            )
                            userProfileList.add(userProfile)
                        }
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