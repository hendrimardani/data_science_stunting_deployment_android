package com.example.stunting.database.with_api.retrofit

import com.example.stunting.database.with_api.request_json.AddingMessageRequestJSON
import com.example.stunting.database.with_api.request_json.AddingUserByGroupIdRequestJSON
import com.example.stunting.database.with_api.request_json.LoginRequestJSON
import com.example.stunting.database.with_api.request_json.RegisterRequestJSON
import com.example.stunting.database.with_api.response.AddingMessageResponse
import com.example.stunting.database.with_api.response.AddingUserByGroupIdResponse
import com.example.stunting.database.with_api.response.AddingUserGroupResponse
import com.example.stunting.database.with_api.response.DeleteUserByIdResponse
import com.example.stunting.database.with_api.response.GetAllBranchesResponse
import com.example.stunting.database.with_api.response.GetAllChecksResponse
import com.example.stunting.database.with_api.response.GetAllMessagesResponse
import com.example.stunting.database.with_api.response.GetAllPregnantMomServicesResponse
import com.example.stunting.database.with_api.response.GetAllUserGroupResponse
import com.example.stunting.database.with_api.response.GetAllUserProfilePatientsResponse
import com.example.stunting.database.with_api.response.GetAllUserProfilesResponse
import com.example.stunting.database.with_api.response.LoginResponse
import com.example.stunting.database.with_api.response.RegisterResponse
import com.example.stunting.database.with_api.response.UpdateGroupByIdResponse
import com.example.stunting.database.with_api.response.UpdateUserProfileByIdResponse
import com.example.stunting.database.with_api.response.UpdateUserProfilePatientByIdResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("messages")
    suspend fun getAllMessages(): Response<GetAllMessagesResponse>

    @POST("user_profile/{user_id}/group/{group_id}/notification")
    suspend fun addMessage(
        @Path("user_id") userId: Int,
        @Path("group_id") groupId: Int,
        @Body addingMessageRequestJSON: AddingMessageRequestJSON
    ): Response<AddingMessageResponse>

    @Multipart
    @PUT("group/{group_id}")
    suspend fun updateGroupById(
        @Path("group_id") groupId: Int,
        @Part("dataJsonString") dataJsonString: RequestBody?,
        @Part gambarProfile: MultipartBody.Part?,
        @Part gambarBanner: MultipartBody.Part?
    ): Response<UpdateGroupByIdResponse>

    @GET("user_profiles/groups")
    suspend fun getAllUserGroup(): Response<GetAllUserGroupResponse>

    @Multipart
    @POST("user_profile/{userIdListString}/group")
    suspend fun addUserGroup(
        @Path("userIdListString") userIdListString: List<Int>,
        @Part("dataJsonString") dataJsonString: RequestBody?,
        @Part gambarProfile: MultipartBody.Part?,
        @Part gambarBanner: MultipartBody.Part?
    ): Response<AddingUserGroupResponse>

    @POST("group/{group_id}")
    suspend fun addUserByGroupId(
        @Path("group_id") groupId: Int,
        @Body addingUserByGroupIdRequestJSON: AddingUserByGroupIdRequestJSON
    ): Response<AddingUserByGroupIdResponse>

    @GET("pregnant_mom_services")
    suspend fun getAllPregnantMomServices(): Response<GetAllPregnantMomServicesResponse>

    @GET("checks")
    suspend fun getAllChecks(): Response<GetAllChecksResponse>

    @DELETE("user/{id}")
    suspend fun deleteUserById(
        @Path("id") id: Int
    ): Response<DeleteUserByIdResponse>

    @Multipart
    @PUT("user_profile_patient/{user_patient_id}")
    suspend fun updateUserProfilePatientById(
        @Path("user_patient_id") userPatientId: Int,
        @Part("dataJsonString") dataJsonString: RequestBody?
    ): Response<UpdateUserProfilePatientByIdResponse>

    @GET("user_profile_patients")
    suspend fun getAllUserProfilePatients(): Response<GetAllUserProfilePatientsResponse>

    @Multipart
    @PUT("user_profile/{user_id}")
    suspend fun updateUserProfileById(
        @Path("user_id") userId: Int,
        @Part("dataJsonString") dataJsonString: RequestBody?,
        @Part gambarProfile: MultipartBody.Part?,
        @Part gambarBanner: MultipartBody.Part?
    ): Response<UpdateUserProfileByIdResponse>

    @GET("user_profiles")
    suspend fun getAllUserProfiles(): Response<GetAllUserProfilesResponse>

    @POST("register")
    suspend fun register(
        @Body requestBody: RegisterRequestJSON
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body requesttBody: LoginRequestJSON
    ): Response<LoginResponse>

    @GET("branches")
    suspend fun getAllBranches(): Response<GetAllBranchesResponse>
}