package com.example.stunting.datastore.chatting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.ChattingDatabase
import com.example.stunting.database.with_api.entities.branch.BranchEntity
import com.example.stunting.database.with_api.entities.category_service.CategoryServiceEntity
import com.example.stunting.database.with_api.entities.checks.ChecksEntity
import com.example.stunting.database.with_api.entities.checks.ChecksRelation
import com.example.stunting.database.with_api.entities.child_service.ChildServiceEntity
import com.example.stunting.database.with_api.entities.children_patient.ChildrenPatientEntity
import com.example.stunting.database.with_api.entities.pregnant_mom_service.PregnantMomServiceEntity
import com.example.stunting.database.with_api.request_json.AddingUserGroupRequestJSON
import com.example.stunting.database.with_api.request_json.LoginRequestJSON
import com.example.stunting.database.with_api.request_json.RegisterRequestJSON
import com.example.stunting.database.with_api.request_json.UpdateUserProfileByIdRequestJSON
import com.example.stunting.database.with_api.retrofit.ApiService
import com.example.stunting.database.with_api.entities.user_profile.UserProfileEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileWithUserRelation
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientRelation
import com.example.stunting.database.with_api.entities.users.UsersEntity
import com.example.stunting.database.with_api.request_json.AddingChildServiceByUserIdRequestJSON
import com.example.stunting.database.with_api.request_json.AddingChildrenPatientByUserPatientIdRequestJSON
import com.example.stunting.database.with_api.request_json.AddingPregnantMomServiceByUserIdRequestJSON
import com.example.stunting.database.with_api.request_json.AddingUserByGroupIdRequestJSON
import com.example.stunting.database.with_api.request_json.UpdateChildrenpatientByUserPatientIdRequestJSON
import com.example.stunting.database.with_api.request_json.UpdateUserProfilePatientByIdRequestJSON
import com.example.stunting.database.with_api.response.AddingChildServiceByUserIdResponse
import com.example.stunting.database.with_api.response.AddingChildrenPatientByUserPatientIdResponse
import com.example.stunting.database.with_api.response.AddingPregnantMomServiceByUserIdResponse
import com.example.stunting.database.with_api.response.AddingUserByGroupIdResponse
import com.example.stunting.database.with_api.response.AddingUserGroupResponse
import com.example.stunting.database.with_api.response.DataBranchesItem
import com.example.stunting.database.with_api.response.DataChecksItem
import com.example.stunting.database.with_api.response.DataChildServicesItem
import com.example.stunting.database.with_api.response.DataChildrenPatientByUserPatientIdItem
import com.example.stunting.database.with_api.response.DataChildrenPatientsItem
import com.example.stunting.database.with_api.response.DataPregnantMomServicesItem
import com.example.stunting.database.with_api.response.DataUserProfilePatientsItem
import com.example.stunting.database.with_api.response.DataUserProfilesItem
import com.example.stunting.database.with_api.response.LoginResponse
import com.example.stunting.database.with_api.response.RegisterResponse
import com.example.stunting.database.with_api.response.UpdateChildrenPatientByUserPatientIdResponse
import com.example.stunting.database.with_api.response.UpdateUserProfileByIdResponse
import com.example.stunting.database.with_api.response.UpdateUserProfilePatientByIdResponse
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocket
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File

class ChattingRepository(
    private val chattingDatabase: ChattingDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    var requestImageProfileFile: RequestBody? = null
    var multipartBodyImageProfile: MultipartBody.Part? = null
    var requestImageBannerFile: RequestBody? = null
    var multipartBodyImageBanner: MultipartBody.Part? = null

    private lateinit var webSocket: WebSocket
//    private var userProfileEntity: UserProfileEntity? = null
//    private var groupsEntity: GroupsEntity? = null
//    private var notificationsEntity: NotificationsEntity? = null
//    private var messagesEntity: MessagesEntity? = null

//    fun connect() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url(BuildConfig.SUPABASE_URL_WEBSOCKET)
//            .build()
//
//        val listener = object : WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                Log.d(TAG, "WebSocket connected")
//
//                val joinPayload = """
//            {
//              "topic": "realtime:public:messages",
//              "event": "phx_join",
//              "ref": "1",
//              "payload": {
//                "config": {
//                  "postgres_changes": [
//                    {
//                      "event": "*",
//                      "schema": "public",
//                      "table": "messages"
//                    }
//                  ]
//                }
//              }
//            }
//        """.trimIndent()
//
//                webSocket.send(joinPayload)
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                Log.d(TAG, "Received: $text")
//
//                val json = JSONObject(text)
//                val event = json.optString("event")
//                if (event == "postgres_changes") {
//                    val record = json
//                        .optJSONObject("payload")
//                        ?.optJSONObject("data")
//                        ?.optJSONObject("record")
//
//                    val userId = record?.getString("user_id")!!.toInt()
//                    val groupId = record?.getString("group_id")!!.toInt()
//                    val notificationsId = record?.getString("notification_id")!!.toInt()
//
//                    val userProfileEntityList = mutableListOf<UserProfileEntity?>()
//
////                    val userProfileWithUserRelation = chattingDatabase.userProfileDao().getUserProfileWithUserById(userId)
////                    userProfileEntity = userProfileWithUserRelation.value?.userProfile
//                    userProfileEntity = UserProfileEntity(userId = userId)
//                    userProfileEntityList.add(userProfileEntity)
//
//                    val groupsEntityList = mutableListOf<GroupsEntity?>()
////                    val userGroupReltion = chattingDatabase.userGroupDao().getUserGroupRelationByGroupId(groupId)
////                    groupsEntity = userGroupReltion.value?.groupsEntity
//                    groupsEntity = GroupsEntity(id = groupId)
//                    groupsEntityList.add(groupsEntity)
//
//                    val notificationEntityList = mutableListOf<NotificationsEntity?>()
////                    notificationsEntity = chattingDatabase.notificationsDao().getNotificationsById(notificationsId).value
//                    notificationsEntity = NotificationsEntity(id = notificationsId)
//                    notificationEntityList.add(notificationsEntity)
//
//                    val messagesEntityList = mutableListOf<MessagesEntity>()
//                    messagesEntityList.add(
//                        MessagesEntity(
//                            user_id = userId,
//                            group_id = groupId,
//                            notification_id = notificationsId,
//                            isi_pesan = record.getString("isi_pesan"),
//                            createdAt = record.getString("created_at"),
//                            updatedAt = record.getString("updated_at")
//                        )
//                    )
//                    CoroutineScope(Dispatchers.IO).launch {
//                        chattingDatabase.userProfileDao().insertUserProfile(userProfileEntityList as List<UserProfileEntity>)
//                        chattingDatabase.groupsDao().insertGroups(groupsEntityList as List<GroupsEntity>)
//                        chattingDatabase.notificationsDao().insertNotifications(notificationEntityList as List<NotificationsEntity>)
//                        chattingDatabase.messagesDao().insertMessages(messagesEntityList as List<MessagesEntity>)
//                    }
//
////                    messagesEntity = chattingDatabase.messagesDao().insertMessages(messagesEntityList as List<MessagesEntity>)
////
////                    MessagesRelation(userProfileEntity!!, groupsEntity!!, notificationsEntity!!, messagesEntity!!)
//                }
//            }
//
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                Log.e(TAG, "WebSocket error: ${t.message}")
//            }
//        }
//
//        webSocket = client.newWebSocket(request, listener)
//    }
//
//    fun disconnect() {
//        if (::webSocket.isInitialized) {
//            webSocket.close(1000, "Closing Websocket connection")
//        }
//    }

//    fun getMessageRelationByGroupId(groupId: Int): LiveData<List<MessagesRelation>> {
//        return chattingDatabase.messagesDao().getMessageRelationByGroupId(groupId)
//    }
//
//    fun getMessagesFromLocal(): LiveData<List<MessagesEntity>> {
//        return chattingDatabase.messagesDao().getMessages()
//    }
//
//    suspend fun getMessagesFromApi(): ResultState<List<DataMessagesItem?>> {
//        return try {
//            val response = apiService.getAllMessages()
//            if (response.isSuccessful) {
//                val data = response.body()?.dataMessages ?: emptyList()
//                withContext(Dispatchers.IO) {
//                    insertMessagesToLocal(data)
//                }
////                Log.d(TAG, "onChattingRepository getMessagesFromApi() : Terpanggil")
//                ResultState.Success(data)
//            } else {
//                ResultState.Error("Gagal ambil data: ${response.message()}")
//            }
//        } catch (e: Exception) {
//            ResultState.Error(e.message ?: "Unknown error")
//        }
//    }
//
//    // Menggunakan entitas pusat relasi
//    private suspend fun insertMessagesToLocal(dataMessagesItemList: List<DataMessagesItem?>) {
//        // hasil: 2025-04-18T14:00:00+07:00
//        val currentDateTime = OffsetDateTime.now(ZoneOffset.of("+07:00"))
//        val dateTimeFormatted = currentDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
//
//        val userProfileList = ArrayList<UserProfileEntity>()
//        val groupsList = ArrayList<GroupsEntity>()
//        val notificationsList = ArrayList<NotificationsEntity>()
//        val messagesList = ArrayList<MessagesEntity>()
//
//        dataMessagesItemList.forEach { item ->
//            val userProfile = item?.userProfile
//            val groups = item?.groups
//            val notifications = item?.notifications
//
//            if (userProfile != null && groups != null && notifications != null) {
//                userProfileList.add(
//                    UserProfileEntity(
//                        id = userProfile.id,
//                        userId = userProfile.userId,
//                        nama = userProfile.nama,
//                        nik = userProfile.nik,
//                        jenisKelamin = userProfile.jenisKelamin,
//                        tglLahir = userProfile.tglLahir,
//                        umur = userProfile.umur,
//                        alamat = userProfile.alamat,
//                        gambarProfile = userProfile.gambarProfile,
//                        gambarBanner = userProfile.gambarBanner,
//                        createdAt = userProfile.createdAt,
//                        updatedAt = userProfile.updatedAt
//                    )
//                )
//
//                groupsList.add(
//                    GroupsEntity(
//                        id = groups.id,
//                        namaGroup = groups.namaGroup,
//                        deskripsi = groups.deskripsi,
//                        gambarProfile = groups.gambarProfile,
//                        gambarBanner = groups.gambarBanner,
//                        createdAt = groups.createdAt,
//                        updatedAt = groups.updatedAt
//                    )
//                )
//
//                notificationsList.add(
//                    NotificationsEntity(
//                        id = notifications.id,
//                        isStatus = notifications.isStatus,
//                        createdAt = dateTimeFormatted,
//                        updatedAt = dateTimeFormatted
//                    )
//                )
//
//                messagesList.add(
//                    MessagesEntity(
//                        user_id = userProfile.userId ?: 0,
//                        group_id = groups.id ?: 0,
//                        notification_id = notifications.id ?: 0,
//                        isi_pesan = item.isiPesan,
//                        createdAt = dateTimeFormatted,
//                        updatedAt = dateTimeFormatted
//                    )
//                )
//            }
//        }
//        withContext(Dispatchers.IO) {
//            chattingDatabase.userProfileDao().insertUserProfile(userProfileList)
//            chattingDatabase.groupsDao().insertGroups(groupsList)
//            chattingDatabase.notificationsDao().insertNotifications(notificationsList)
//            chattingDatabase.messagesDao().insertMessages(messagesList)
//        }
//    }
//
//    suspend fun addMessage(userId: Int, groupId: Int, isiPesan: String): ResultState<AddingMessageResponse?> {
//        return try {
//            val requestBody = AddingMessageRequestJSON(isiPesan)
//            val response = apiService.addMessage(userId, groupId, requestBody)
//
//            if (response.isSuccessful) {
//                ResultState.Success(response.body())
//            } else {
//                if (response.code() == 401) {
//                    ResultState.Unauthorized
//                } else {
//                    val errorBody = response.errorBody()?.string()
////                    Log.e(TAG, "onChattingRepository addMessage Error ${response.code()}: $errorBody")
//                    ResultState.Error("Error ${response.code()}: $errorBody")
//                }
//            }
//        } catch (e: HttpException) {
////            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
//            ResultState.Error("Exception: ${e.message}")
//        } catch (e: Exception) {
////            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
//            ResultState.Error("Unexpected error: ${e.message}")
//        }
//    }
//
//    fun getUserGroupRelationByUserIdRole(userId: Int, role: String?): LiveData<List<UserGroupRelation>> {
//        return chattingDatabase.userGroupDao().getUserGroupRelationByUserIdRole(userId, role!!)
//    }
//
//    fun getUserGroupRelationByUserIdGroupId(userId: Int, groupId: Int): LiveData<UserGroupRelation> {
//        return chattingDatabase.userGroupDao().getUserGroupRelationByUserIdGroupId(userId, groupId)
//    }
//
//    fun getUserGroupRelationByGroupIdList(groupId: Int): LiveData<List<UserGroupRelation>> {
//        return chattingDatabase.userGroupDao().getUserGroupRelationByGroupIdList(groupId)
//    }
//
//    fun getUserGroupRelationByGroupId(groupId: Int): LiveData<UserGroupRelation> {
//        return chattingDatabase.userGroupDao().getUserGroupRelationByGroupId(groupId)
//    }
//
//    fun getUserGroupRelationByUserId(userId: Int): LiveData<List<UserGroupRelation>> {
//        return chattingDatabase.userGroupDao().getUserGroupRelationByUserId(userId)
//    }
//
//    suspend fun updateGroupById(
//        groupId: Int, userId: Int, namaGroup: String?, deskripsi: String?, gambarProfile: File?, gambarBanner: File?
//    ): ResultState<UpdateGroupByIdResponse?> {
//        return try {
//            val requestBodyJson = UpdateGroupByIdRequestJSON(userId, namaGroup, deskripsi)
//
//            // Convert dataJson menjadi JSON String
//            val gson = GsonBuilder()
//                .serializeNulls() // Boleh null
//                .create()
//            val jsonData = gson.toJson(requestBodyJson)
//
//            // Convert JSON string to RequestBody
//            val dataRequestBody = jsonData.toRequestBody("text/plain".toMediaTypeOrNull())
//
//            if (gambarProfile != null) {
//                requestImageProfileFile = gambarProfile.asRequestBody("image/*".toMediaTypeOrNull())
//                multipartBodyImageProfile = MultipartBody.Part.createFormData(
//                    "gambar_profile",
//                    gambarProfile.name,
//                    requestImageProfileFile!!
//                )
//            }
//
//            if (gambarBanner != null) {
//                requestImageBannerFile = gambarBanner.asRequestBody("image/*".toMediaTypeOrNull())
//                multipartBodyImageBanner = MultipartBody.Part.createFormData(
//                    "gambar_banner",
//                    gambarBanner.name,
//                    requestImageBannerFile!!
//                )
//            }
//            val response = apiService.updateGroupById(
//                groupId, dataRequestBody, multipartBodyImageProfile, multipartBodyImageBanner
//            )
//
//            if (response.isSuccessful) {
////                Log.d(TAG, "onChattingRepository updateGroupById() Success ${response.code()}: $response")
//                val updateGroupsByIdList = response.body()?.dataUpdateGroupById
//                val updatedGroupsEntity = updateGroupsByIdList?.map { group ->
//                    GroupsEntity(
//                        id = group?.id,
//                        namaGroup = group?.namaGroup,
//                        deskripsi = group?.deskripsi,
//                        gambarProfile = group?.gambarProfile,
//                        gambarBanner = group?.gambarBanner,
//                        createdAt = group?.createdAt,
//                        updatedAt = group?.updatedAt
//                    )
//                }
//                if (updateGroupsByIdList != null) {
//                    withContext(Dispatchers.IO) {
//                        chattingDatabase.groupsDao().insertGroups(updatedGroupsEntity!!)
//                    }
//                }
//                ResultState.Success(response.body())
//            } else {
//                if (response.code() == 401) {
//                    ResultState.Unauthorized
//                } else {
//                    val errorBodyJson = response.errorBody()?.string()
////                    Log.e(TAG, "onChattingRepository updateGroupById() Error ${response.code()}: $errorBodyJson")
//                    // Ubah dari JSON string ke JSON
//                    val jsonObject = JSONObject(errorBodyJson!!)
//                    val message = jsonObject.getString("message")
//                    ResultState.Error(message)
//                }
//            }
//        } catch (e: HttpException) {
////            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
//            ResultState.Error("Exception: ${e.message}")
//        } catch (e: Exception) {
////            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
//            ResultState.Error("Unexpected error: ${e.message}")
//        }
//    }
//
//    fun getUserGroupsFromLocal(): LiveData<List<UserGroupEntity>> {
//        return chattingDatabase.userGroupDao().getUserGroup()
//    }
//
//    suspend fun getUserGroupsFromApi(): ResultState<List<UserGroupsItem?>> {
//        return try {
//            val response = apiService.getAllUserGroup()
//            if (response.isSuccessful) {
//                val data = response.body()?.userGroups ?: emptyList()
//                withContext(Dispatchers.IO) {
//                    insertUserGroupsToLocal(data)
//                }
//                ResultState.Success(data)
//            } else {
//                ResultState.Error("Gagal ambil data: ${response.message()}")
//            }
//        } catch (e: Exception) {
//            ResultState.Error(e.message ?: "Unknown error")
//        }
//    }
//
//    // Menggunakan entitas pusat relasi
//    private suspend fun insertUserGroupsToLocal(userGroupsItemList: List<UserGroupsItem?>) {
//        val groupList = ArrayList<GroupsEntity>()
//        val userProfileList = ArrayList<UserProfileEntity>()
//        val userGroupList = ArrayList<UserGroupEntity>()
//
//        userGroupsItemList.forEach { item ->
//            val userProfile = item?.userProfile
//            val groups = item?.groups
//
//            if (groups != null && userProfile != null) {
//                userProfileList.add(
//                    UserProfileEntity(
//                        id = userProfile.id,
//                        userId = userProfile.userId,
//                        nama = userProfile.nama,
//                        nik = userProfile.nik,
//                        jenisKelamin = userProfile.jenisKelamin,
//                        tglLahir = userProfile.tglLahir,
//                        umur = userProfile.umur,
//                        alamat = userProfile.alamat,
//                        gambarProfile = userProfile.gambarProfile,
//                        gambarBanner = userProfile.gambarBanner,
//                        createdAt = userProfile.createdAt,
//                        updatedAt = userProfile.updatedAt
//                    )
//                )
//                groupList.add(
//                    GroupsEntity(
//                        id = groups.id,
//                        namaGroup = groups.namaGroup,
//                        deskripsi = groups.deskripsi,
//                        gambarProfile = groups.gambarProfile,
//                        gambarBanner = groups.gambarBanner,
//                        createdAt = groups.createdAt,
//                        updatedAt = groups.updatedAt
//                    )
//                )
//                userGroupList.add(
//                    UserGroupEntity(
//                        group_id = groups.id ?: 0,
//                        user_id = userProfile.userId ?: 0,
//                        role = item.role,
//                        createdBy = item.createdBy,
//                        createdAt = item.createdAt,
//                        updatedAt = item.updatedAt
//                    )
//                )
//            }
//        }
//        withContext(Dispatchers.IO) {
//            chattingDatabase.groupsDao().insertGroups(groupList)
//            chattingDatabase.userProfileDao().insertUserProfile(userProfileList)
//            chattingDatabase.userGroupDao().insertUserGroups(userGroupList)
//        }
//    }

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

    suspend fun addUserByGroupId(groupId: Int, userId: List<Int>, role: String?): ResultState<AddingUserByGroupIdResponse?> {
        return try {
            val requestBody = AddingUserByGroupIdRequestJSON(userId, role)
            val response = apiService.addUserByGroupId(groupId, requestBody)

            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJson = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository addUserByGroupId Error ${response.code()}: $errorBodyJson")
                    // Ubah dari JSON string ke JSON
                    val jsonObject = JSONObject(errorBodyJson!!)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
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
    fun getChecksRelationByUserIdCategoryServiceIdChildService(userId: Int, categoryServiceId: Int): LiveData<List<ChecksRelation>> =
        chattingDatabase.checksDao().getChecksRelationByUserIdCategoryServiceIdChildService(userId, categoryServiceId)

    fun getChildrenPatientByNamaAnak(namaAnak: String) =
        chattingDatabase.childrenPatientDao().getChildrenPatientByNamaAnak(namaAnak)

    fun getChildrenPatientsFromLocal() = chattingDatabase.childrenPatientDao().getChildrenPatientsFromLocal()

    suspend fun getChildrenPatientsFromApi(): ResultState<List<DataChildrenPatientsItem?>> {
        return try {
            val response = apiService.getAllChildrenPatients()
            if (response.isSuccessful) {
                val data = response.body()?.dataChildrenPatients ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertChildrenPatientsToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    // Menggunakan entitas pusat relasi
    private suspend fun insertChildrenPatientsToLocal(dataChildrenPatientsItem: List<DataChildrenPatientsItem?>) {
        val userProfilePatientsList = ArrayList<UserProfilePatientEntity>()
        val childrenPatientsList = ArrayList<ChildrenPatientEntity>()

        dataChildrenPatientsItem.forEach { item ->
            val userProfilePatientEntity = item?.userProfilePatient

            if (userProfilePatientEntity != null) {
                userProfilePatientsList.add(
                    UserProfilePatientEntity(
                        id = userProfilePatientEntity.id,
                        userPatientId = userProfilePatientEntity.userPatientId,
                        branchId = userProfilePatientEntity.branchId,
                        namaBumil = userProfilePatientEntity.namaBumil,
                        nikBumil = userProfilePatientEntity.nikBumil,
                        tglLahirBumil = userProfilePatientEntity.tglLahirBumil,
                        umurBumil = userProfilePatientEntity.umurBumil,
                        alamat = userProfilePatientEntity.alamat,
                        namaAyah = userProfilePatientEntity.namaAyah,
                        gambarProfile = userProfilePatientEntity.gambarProfile,
                        gambarBanner = userProfilePatientEntity.gambarBanner,
                        createdAt = userProfilePatientEntity.createdAt,
                        updatedAt = userProfilePatientEntity.updatedAt
                    )
                )
                childrenPatientsList.add(
                    ChildrenPatientEntity(
                        id = item.id,
                        userPatientId = item.userPatientId,
                        namaAnak = item.namaAnak,
                        nikAnak = item.nikAnak,
                        jenisKelaminAnak = item.jenisKelaminAnak,
                        tglLahirAnak = item.tglLahirAnak,
                        umurAnak = item.umurAnak,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt
                    )
                )
            }
        }
        withContext(Dispatchers.IO) {
            chattingDatabase.userProfilePatientDao().insertUserProfilePatients(userProfilePatientsList)
            chattingDatabase.childrenPatientDao().insertChildrenPatients(childrenPatientsList)
        }
    }

    suspend fun insertChildServices(childServicesList: List<ChildServiceEntity>) =
        chattingDatabase.childServiceDao().insertChildServices(childServicesList)

    suspend fun addChildServiceByUserId(
        userId: Int, categoryServiceId: Int, catatan: String?, namaAnak: String,
        tinggiCm: String, hasilPemeriksaan: String
    ): ResultState<AddingChildServiceByUserIdResponse?> {
        return try {
            val requestBody = AddingChildServiceByUserIdRequestJSON(
                categoryServiceId, catatan, namaAnak, tinggiCm, hasilPemeriksaan
            )
            val response = apiService.addChildSericeByUserId(userId, requestBody)

            if (response.isSuccessful) {
//                Log.e(TAG, "onChattingRepository addChildServiceByUserId Success ${response.code()} : ${response.body()}")
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJson = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository addChildServiceByUserId Error ${response.code()}: $errorBodyJson")
                    val jsonObject = JSONObject(errorBodyJson!!)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
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

    suspend fun insertPregnantMomServices(pregnantMomServicesList: List<PregnantMomServiceEntity>) =
        chattingDatabase.pregnantMomServiceDao().insertPregnantMomServices(pregnantMomServicesList)

    suspend fun addPregnantMomServiceByUserId(
        userId: Int, categoryServiceId: Int, catatan: String?, namaBumil: String, hariPertamaHaidTerakhir: String,
        tglPerkiraanLahir: String, umurKehamilan: String, statusGiziKesehatan: String
    ): ResultState<AddingPregnantMomServiceByUserIdResponse?> {
        return try {
            val requestBody = AddingPregnantMomServiceByUserIdRequestJSON(
                categoryServiceId, catatan, namaBumil, hariPertamaHaidTerakhir,
                tglPerkiraanLahir, umurKehamilan, statusGiziKesehatan)
            val response = apiService.addPregnantMomServiceByUserId(userId, requestBody)

            if (response.isSuccessful) {
//                Log.e(TAG, "onChattingRepository addPregnantMomServiceByUserId Success ${response.code()} : ${response.body()}")
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJson = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository addPregnantMomServiceByUserId Error ${response.code()}: $errorBodyJson")
                    val jsonObject = JSONObject(errorBodyJson!!)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
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

    fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearchAnak(userPatientId: Int, categoryServiceId: Int, searchQuery: String) =
        chattingDatabase.checksDao().getChecksRelationByUserPatientIdCategoryServiceIdWithSearchAnak(userPatientId, categoryServiceId, searchQuery)

    suspend fun getChildServiceFromApi(): ResultState<List<DataChildServicesItem?>> {
        return try {
            val response = apiService.getAllChildServices()
            if (response.isSuccessful) {
                val data = response.body()?.dataChildServices ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertChildServiceToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    // Menggunakan entitas pusat relasi
    private suspend fun insertChildServiceToLocal(dataChildServicesItemList: List<DataChildServicesItem?>) {
        val childServicesList = ArrayList<ChildServiceEntity>()

        dataChildServicesItemList.forEach { item ->
            childServicesList.add(
                ChildServiceEntity(
                    id = item?.id,
                    pemeriksaanId = item?.pemeriksaanId,
                    tinggiCm = item?.tinggiCm,
                    hasilPemeriksaan = item?.hasilPemeriksaan,
                    createdAt = item?.createdAt,
                    updatedAt = item?.updatedAt
                )
            )
        }
        withContext(Dispatchers.IO) {
            chattingDatabase.childServiceDao().insertChildServices(childServicesList)
        }
    }

    suspend fun getPregnantMomServiceFromApi(): ResultState<List<DataPregnantMomServicesItem?>> {
        return try {
            val response = apiService.getAllPregnantMomServices()
            if (response.isSuccessful) {
                val data = response.body()?.dataPregnantMomServices ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertPregnantMomServiceToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    // Menggunakan entitas pusat relasi
    private suspend fun insertPregnantMomServiceToLocal(dataPregnantMomServicesItemList: List<DataPregnantMomServicesItem?>) {
        val pregnantMomServiceList = ArrayList<PregnantMomServiceEntity>()

        dataPregnantMomServicesItemList.forEach { item ->
            pregnantMomServiceList.add(
                PregnantMomServiceEntity(
                    id = item?.id,
                    pemeriksaanId = item?.pemeriksaanId,
                    hariPertamaHaidTerakhir = item?.hariPertamaHaidTerakhir,
                    tglPerkiraanLahir = item?.tglPerkiraanLahir,
                    umurKehamilan = item?.umurKehamilan,
                    statusGiziKesehatan = item?.statusGiziKesehatan,
                    createdAt = item?.createdAt,
                    updatedAt = item?.updatedAt
                )
            )
        }
        withContext(Dispatchers.IO) {
            chattingDatabase.pregnantMomServiceDao().insertPregnantMomServices(pregnantMomServiceList)
        }
    }

    fun getTransactionCountByMonth() = chattingDatabase.checksDao().getTransactionCountByMonth()

    fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(userPatientId: Int, categoryServiceId: Int, searchQuery: String) =
        chattingDatabase.checksDao().getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(userPatientId, categoryServiceId, searchQuery)

    suspend fun insertChecks(checksList: List<ChecksEntity>) =
        chattingDatabase.checksDao().insertChecks(checksList)

    suspend fun getChecksFromApi(): ResultState<List<DataChecksItem?>> {
        return try {
            val response = apiService.getAllChecks()
            if (response.isSuccessful) {
                val data = response.body()?.dataChecks ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertChecksToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    // Menggunakan entitas pusat relasi
    private suspend fun insertChecksToLocal(dataChecksItemList: List<DataChecksItem?>) {
        val userProfilesList = ArrayList<UserProfileEntity>()
        val userProfilePatientsList = ArrayList<UserProfilePatientEntity>()
        val childrenPatientsList = ArrayList<ChildrenPatientEntity>()
        val categoryServicesList = ArrayList<CategoryServiceEntity>()
        val checksList = ArrayList<ChecksEntity>()

        dataChecksItemList.forEach { item ->
            val userProfileEntity = item?.userProfile
            val userProfilePatientEntity = item?.userProfilePatient
            val childrenPatientEntity = item?.childrenPatient
            val categoryServiceEntity = item?.categoryService

            if (userProfileEntity != null && userProfilePatientEntity != null &&
                childrenPatientEntity != null && categoryServiceEntity != null
                ) {
                userProfilesList.add(
                    UserProfileEntity(
                        id = userProfileEntity.id,
                        userId = userProfileEntity.userId,
                        branchId = userProfileEntity.branchId,
                        nama = userProfileEntity.nama,
                        jenisKelamin = userProfileEntity.jenisKelamin,
                        nik = userProfileEntity.nik,
                        tglLahir = userProfileEntity.tglLahir,
                        umur = userProfileEntity.umur,
                        alamat = userProfileEntity.alamat,
                        gambarProfile = userProfileEntity.gambarProfile,
                        gambarBanner = userProfileEntity.gambarBanner,
                        createdAt = userProfileEntity.createdAt,
                        updatedAt = userProfileEntity.updatedAt
                    )
                )
                userProfilePatientsList.add(
                    UserProfilePatientEntity(
                        id = userProfilePatientEntity.id,
                        userPatientId = userProfilePatientEntity.userPatientId,
                        branchId = userProfilePatientEntity.branchId,
                        namaBumil = userProfilePatientEntity.namaBumil,
                        nikBumil = userProfilePatientEntity.nikBumil,
                        tglLahirBumil = userProfilePatientEntity.tglLahirBumil,
                        umurBumil = userProfilePatientEntity.umurBumil,
                        alamat = userProfilePatientEntity.alamat,
                        namaAyah = userProfilePatientEntity.namaAyah,
                        gambarProfile = userProfilePatientEntity.gambarProfile,
                        gambarBanner = userProfilePatientEntity.gambarBanner,
                        createdAt = userProfilePatientEntity.createdAt,
                        updatedAt = userProfilePatientEntity.updatedAt
                    )
                )
                childrenPatientsList.add(
                    ChildrenPatientEntity(
                        id = childrenPatientEntity.id,
                        userPatientId = childrenPatientEntity.userPatientId,
                        namaAnak  = childrenPatientEntity.namaAnak,
                        nikAnak = childrenPatientEntity.nikAnak,
                        jenisKelaminAnak = childrenPatientEntity.jenisKelaminAnak,
                        tglLahirAnak = childrenPatientEntity.tglLahirAnak,
                        umurAnak = childrenPatientEntity.umurAnak,
                        createdAt = childrenPatientEntity.createdAt,
                        updatedAt = childrenPatientEntity.updatedAt
                    )
                )
                categoryServicesList.add(
                    CategoryServiceEntity(
                        id = categoryServiceEntity.id,
                        namaLayanan = categoryServiceEntity.namaLayanan,
                        createdAt = categoryServiceEntity.createdAt,
                        updatedAt = categoryServiceEntity.updatedAt,
                    )
                )
                checksList.add(
                    ChecksEntity(
                        id = item.id ?: 0,
                        userId = item.userId ?: 0,
                        userPatientId = item.userPatientId ?: 0,
                        childrenPatientId = item.childrenPatientId ?: 0,
                        categoryServiceId = item.categoryServiceId ?: 0,
                        tglPemeriksaan = item.tglPemeriksaan,
                        catatan = item.catatan,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt
                    )
                )
            }
        }
        withContext(Dispatchers.IO) {
            chattingDatabase.userProfileDao().insertUserProfiles(userProfilesList)
            chattingDatabase.userProfilePatientDao().insertUserProfilePatients(userProfilePatientsList)
            chattingDatabase.childrenPatientDao().insertChildrenPatients(childrenPatientsList)
            chattingDatabase.categoryServiceDao().insertCategoryServices(categoryServicesList)
            chattingDatabase.checksDao().insertChecks(checksList)
        }
    }

    fun getBranchById(id: Int): LiveData<List<BranchEntity>> =
        chattingDatabase.branchDao().getBranchById(id)

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

    suspend fun updateUserProfilePatientByIdFromLocal(userProfilePatientEntity: UserProfilePatientEntity) =
        chattingDatabase.userProfilePatientDao().updateUserProfilePatientByIdFromLocal(userProfilePatientEntity)

    suspend fun updateUserProfilePatientByIdFromApi(
        userPatientId: Int, namaCabang: String, namaBumil: String, nikBumil: String, tglLahirBumil: String,
        umurBumil: String, alamat: String, namaAyah: String
    ): ResultState<UpdateUserProfilePatientByIdResponse?> {
        return try {
            val requestBodyJson = UpdateUserProfilePatientByIdRequestJSON(
                namaCabang, namaBumil, nikBumil, tglLahirBumil, umurBumil, alamat, namaAyah
            )

            // Convert dataJson menjadi JSON String
            val gson = GsonBuilder()
                .serializeNulls() // Boleh null
                .create()
            val jsonData = gson.toJson(requestBodyJson)

            // Convert JSON string to RequestBody
            val dataRequestBody = jsonData.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.updateUserProfilePatientById(
                userPatientId, dataRequestBody
            )

            if (response.isSuccessful) {
                val dataUpdateUserProfilePatientById = response.body()?.dataUpdateUserProfilePatientById
                if (dataUpdateUserProfilePatientById != null) {
                    val updatedUserProfilePatientEntity = UserProfilePatientEntity(
                        id = dataUpdateUserProfilePatientById.id,
                        userPatientId = dataUpdateUserProfilePatientById.userPatientId,
                        branchId = dataUpdateUserProfilePatientById.branchId,
                        namaBumil = dataUpdateUserProfilePatientById.namaBumil,
                        nikBumil = dataUpdateUserProfilePatientById.nikBumil,
                        tglLahirBumil = dataUpdateUserProfilePatientById.tglLahirBumil,
                        umurBumil = dataUpdateUserProfilePatientById.umurBumil,
                        alamat = dataUpdateUserProfilePatientById.alamat,
                        namaAyah = dataUpdateUserProfilePatientById.namaAyah,
                        gambarProfile = dataUpdateUserProfilePatientById.gambarProfile,
                        gambarBanner = dataUpdateUserProfilePatientById.gambarBanner,
                        createdAt = dataUpdateUserProfilePatientById.createdAt,
                        updatedAt = dataUpdateUserProfilePatientById.updatedAt
                    )
                    withContext(Dispatchers.IO) {
                        chattingDatabase.userProfilePatientDao().updateUserProfilePatientByIdFromLocal(updatedUserProfilePatientEntity)
                    }
                }
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJson = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository updateUserProfilePatientByIdFromApi Error ${response.code()}: $errorBodyJson")
                    // Ubah dari JSON string ke JSON
                    val jsonObject = JSONObject(errorBodyJson!!)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
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

    suspend fun updateChildrenPatientByUserPatientIdFromLocal(
        userPatientId: Int, namaAnak: String, nikAnak: String, jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ) =
        chattingDatabase.childrenPatientDao().updateChildrenPatientByUserPatientIdFromLocal(
            userPatientId, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak
        )

    suspend fun updateChildrenPatientByUserPatientIdFromApi(
        userPatientId: Int, namaAnak: String, nikAnak: String,
        jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ): ResultState<UpdateChildrenPatientByUserPatientIdResponse?> {
        return try {
            val requestBodyJson = UpdateChildrenpatientByUserPatientIdRequestJSON(
                namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak
            )
            val response = apiService.updateChildrenPatientByUserPatientId(
                userPatientId, requestBodyJson
            )

            if (response.isSuccessful) {
                val dataUpdateChildrenPatienttByUserPatientId = response.body()?.dataUpdateChildrenPatienttByUserPatientId
                if (dataUpdateChildrenPatienttByUserPatientId != null) {
                    withContext(Dispatchers.IO) {
                        chattingDatabase.childrenPatientDao().updateChildrenPatientByUserPatientIdFromLocal(
                            userPatientId, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak
                        )
                    }
                }
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJson = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository updateUserProfilePatientByIdFromApi Error ${response.code()}: $errorBodyJson")
                    // Ubah dari JSON string ke JSON
                    val jsonObject = JSONObject(errorBodyJson!!)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
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

    fun getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId: Int, userPatientId: Int):
            LiveData<ChildrenPatientEntity> =
        chattingDatabase.childrenPatientDao().getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId, userPatientId)

    fun getChildrenPatientByUserPatientIdFromLocal(userPatientId: Int): LiveData<List<ChildrenPatientEntity>> =
        chattingDatabase.childrenPatientDao().getChildrenPatientByUserPatientIdFromLocal(userPatientId)

    suspend fun getChildrenPatientByUserPatientIdFromApi(userPatientId: Int): ResultState<List<DataChildrenPatientByUserPatientIdItem?>> {
        return try {
            val response = apiService.getChildrenPatientByUserPatientId(userPatientId)
            if (response.isSuccessful) {
                val data = response.body()?.dataChildrenPatientByUserPatientId ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertChildrenPatientByUserPatientIdToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun insertChildrenPatientByUserPatientIdToLocal(dataChildrenPatientByUserPatientIdItemList: List<DataChildrenPatientByUserPatientIdItem?>) {
        val childrenPatientList = ArrayList<ChildrenPatientEntity>()
        dataChildrenPatientByUserPatientIdItemList.forEach { item ->
            if (item != null) {
                childrenPatientList.add(
                    ChildrenPatientEntity(
                        id = item.id,
                        userPatientId = item.userPatientId,
                        namaAnak = item.namaAnak,
                        nikAnak = item.nikAnak,
                        jenisKelaminAnak = item.jenisKelaminAnak,
                        tglLahirAnak = item.tglLahirAnak,
                        umurAnak = item.umurAnak,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt
                    )
                )
            }
        }
        withContext(Dispatchers.IO) {
            chattingDatabase.childrenPatientDao().insertChildrenPatients(childrenPatientList)
        }
    }

    suspend fun addChildrenPatientByUserPatientId(
        userPatientId: Int, namaAnak: String, nikAnak: String, jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ): ResultState<AddingChildrenPatientByUserPatientIdResponse?> {
        return try {
            val requestBody = AddingChildrenPatientByUserPatientIdRequestJSON(
                namaAnak, nikAnak, jenisKelaminAnak,tglLahirAnak, umurAnak
            )
            val response = apiService.addChildrenPatientByUserPatientId(userPatientId, requestBody)

            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJsonString = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository addChildrenPatientByUserPatientId Error ${response.code()}: $errorBody")
                    val jsonObject = JSONObject(errorBodyJsonString)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
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

    fun getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId: Int): LiveData<UserProfilePatientRelation> =
        chattingDatabase.userProfilePatientDao().getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId)

    fun getChecksRelationByUserIdCategoryServiceIdPregnantMomService(userId: Int, categoryServiceId: Int): LiveData<List<ChecksRelation>> =
        chattingDatabase.checksDao().getChecksRelationByUserIdCategoryServiceIdPregnantMomService(userId, categoryServiceId)

    fun getUserProfilePatientByNamaBumil(namaBumil: String): LiveData<UserProfilePatientEntity> =
        chattingDatabase.userProfilePatientDao().getUserProfilePatientByNamaBumil(namaBumil)

    fun getUserProfilePatientsFromLocal(): LiveData<List<UserProfilePatientEntity>> =
        chattingDatabase.userProfilePatientDao().getUserProfilePatientsFromLocal()

    suspend fun getUserProfilePatientsFromApi(): ResultState<List<DataUserProfilePatientsItem?>> {
        return try {
            val response = apiService.getAllUserProfilePatients()
            if (response.isSuccessful) {
                val data = response.body()?.dataUserProfilePatients ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertUserProfilePatientsToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    // Menggunakan entitas pusat relasi
    private suspend fun insertUserProfilePatientsToLocal(dataUserProfilePatiensItem: List<DataUserProfilePatientsItem?>) {
        val usersList = ArrayList<UsersEntity>()
        val branchesList = ArrayList<BranchEntity>()
        val userProfilePatientsList = ArrayList<UserProfilePatientEntity>()

        dataUserProfilePatiensItem.forEach { item ->
            val usersEntity = item?.users
            val branchEntity = item?.branch

            if (usersEntity != null && branchEntity != null) {
                usersList.add(
                    UsersEntity(
                        id = usersEntity.id,
                        email = usersEntity.email,
                        password = usersEntity.password,
                        role = usersEntity.role,
                        createdAt = usersEntity.createdAt,
                        updatedAt = usersEntity.updatedAt
                    )
                )
                branchesList.add(
                    BranchEntity(
                        id = branchEntity.id,
                        namaCabang = branchEntity.namaCabang,
                        noTlp = branchEntity.noTlp,
                        alamat = branchEntity.alamat,
                        createdAt = branchEntity.createdAt,
                        updatedAt = branchEntity.updatedAt
                    )
                )
                userProfilePatientsList.add(
                    UserProfilePatientEntity(
                        id = item.id,
                        userPatientId = item.userPatientId,
                        branchId = item.branchId,
                        namaBumil = item.namaBumil,
                        nikBumil = item.nikBumil,
                        tglLahirBumil = item.tglLahirBumil,
                        umurBumil = item.umurBumil,
                        namaAyah = item.namaAyah,
                        alamat = item.alamat,
                        gambarProfile = item.gambarProfile,
                        gambarBanner = item.gambarBanner,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt,
                    )
                )
            }
        }
        withContext(Dispatchers.IO) {
            chattingDatabase.usersDao().insertUsers(usersList)
            chattingDatabase.branchDao().insertBranches(branchesList)
            chattingDatabase.userProfilePatientDao().insertUserProfilePatients(userProfilePatientsList)
        }
    }

    suspend fun updateUserProfileById(
        userId: Int, nama: String, nik: String, umur: String, alamat: String, jenisKelamin: String,
        tglLahir: String, gambarProfile: File?, gambarBanner: File?
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
                val updatedUserProfileByIdList = response.body()?.dataUpdateUserProfileById
                val updatedUserProfileEntity = updatedUserProfileByIdList?.map { userProfile ->
                    UserProfileEntity(
                        id = userProfile?.id,
                        userId = userProfile?.userId,
                        nama = userProfile?.nama,
                        nik = userProfile?.nik,
                        jenisKelamin = userProfile?.jenisKelamin,
                        tglLahir = userProfile?.tglLahir,
                        umur = userProfile?.umur,
                        alamat = userProfile?.alamat,
                        gambarProfile = userProfile?.gambarProfile,
                        gambarBanner = userProfile?.gambarBanner,
                        createdAt = userProfile?.createdAt,
                        updatedAt = userProfile?.updatedAt
                    )
                }
                if (updatedUserProfileByIdList != null) {
                    withContext(Dispatchers.IO) {
                        chattingDatabase.userProfileDao().insertUserProfiles(updatedUserProfileEntity!!)
                    }
                }
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJson = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository updateUserProfileById() Error ${response.code()}: $errorBodyJson")
                    // Ubah dari JSON string ke JSON
                    val jsonObject = JSONObject(errorBodyJson!!)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
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

    fun getUserProfileRelationByUserIdFromLocal(userId: Int): LiveData<UserProfileWithUserRelation> =
        chattingDatabase.userProfileDao().getUserProfileRelationByUserIdFromLocal(userId)


    fun getUserProfilesFromLocal(): LiveData<List<UserProfileEntity>> =
        chattingDatabase.userProfileDao().getUserProfilesFromLocal()

    suspend fun getUserProfilesFromApi(): ResultState<List<DataUserProfilesItem?>> {
        return try {
            val response = apiService.getAllUserProfiles()
            if (response.isSuccessful) {
                val data = response.body()?.dataUserProfiles ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertUserProfilesToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    // Menggunakan entitas pusat relasi
    private suspend fun insertUserProfilesToLocal(dataUserProfilesItem: List<DataUserProfilesItem?>) {
        val usersList = ArrayList<UsersEntity>()
        val branchesList = ArrayList<BranchEntity>()
        val userProfileList = ArrayList<UserProfileEntity>()

        dataUserProfilesItem.forEach { item ->
            val usersEntity = item?.users
            val branchEntity = item?.branch

            if (usersEntity != null && branchEntity != null) {
                usersList.add(
                    UsersEntity(
                        id = usersEntity.id,
                        email = usersEntity.email,
                        password = usersEntity.password,
                        role = usersEntity.role,
                        createdAt = usersEntity.createdAt,
                        updatedAt = usersEntity.updatedAt
                    )
                )
                branchesList.add(
                    BranchEntity(
                        id = branchEntity.id,
                        namaCabang = branchEntity.namaCabang,
                        noTlp = branchEntity.noTlp,
                        alamat = branchEntity.alamat,
                        createdAt = branchEntity.createdAt,
                        updatedAt = branchEntity.updatedAt
                    )
                )
                userProfileList.add(
                    UserProfileEntity(
                        id = item.id,
                        userId = item.userId,
                        branchId = item.branchId,
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
        withContext(Dispatchers.IO) {
            chattingDatabase.usersDao().insertUsers(usersList)
            chattingDatabase.branchDao().insertBranches(branchesList)
            chattingDatabase.userProfileDao().insertUserProfiles(userProfileList)
        }
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

    suspend fun register(
        nama: String, email: String, namaCabang: String, password: String, repeatPassword: String
    ): ResultState<RegisterResponse?> {
        return try {
            val requestBody = RegisterRequestJSON(nama, email, namaCabang, password, repeatPassword)
            val response = apiService.register(requestBody)

            if (response.isSuccessful) {
//                Log.e(TAG, "onChattingRepository register Success ${response.code()} : ${response.body()}")
                ResultState.Success(response.body())
            } else {
                if (response.code() == 401) {
                    ResultState.Unauthorized
                } else {
                    val errorBodyJson = response.errorBody()?.string()
//                    Log.e(TAG, "onChattingRepository register Error ${response.code()}: $errorBodyJson")
                    val jsonObject = JSONObject(errorBodyJson!!)
                    val message = jsonObject.getString("message")
                    ResultState.Error(message)
                }
            }
        } catch (e: HttpException) {
//            Log.e(TAG, "onChattingRepository Exception: ${e.message}", e)
            ResultState.Error("Exception: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "onChattingRepository General Exception: ${e.message}", e)
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    fun getBranchesFromLocal(): LiveData<List<BranchEntity>> =
        chattingDatabase.branchDao().getBranches()

    suspend fun getBranchesFromApi(): ResultState<List<DataBranchesItem?>> {
        return try {
            val response = apiService.getAllBranches()
            if (response.isSuccessful) {
                val data = response.body()?.dataBranches ?: emptyList()
                withContext(Dispatchers.IO) {
                    insertBranchesToLocal(data)
                }
                ResultState.Success(data)
            } else {
                ResultState.Error("Gagal ambil data: ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    // Menggunakan entitas pusat relasi
    private suspend fun insertBranchesToLocal(dataBranchesItem: List<DataBranchesItem?>) {
        val branchsList = ArrayList<BranchEntity>()

        dataBranchesItem.forEach { item ->
            if (item != null) {
                branchsList.add(
                    BranchEntity(
                        id = item.id,
                        namaCabang = item.namaCabang,
                        noTlp = item.noTlp,
                        alamat = item.alamat,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt
                    )
                )
            }
        }
        withContext(Dispatchers.IO) {
            chattingDatabase.branchDao().insertBranches(branchsList)
        }
    }

    companion object {
        private val TAG = ChattingRepository::class.java.simpleName
    }
}