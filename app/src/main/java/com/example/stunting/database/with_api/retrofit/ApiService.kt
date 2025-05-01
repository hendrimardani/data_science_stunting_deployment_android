package com.example.stunting.database.with_api.retrofit

import com.example.stunting.database.with_api.request_json.AddingMessageRequestJSON
import com.example.stunting.database.with_api.request_json.AddingUserGroupRequestJSON
import com.example.stunting.database.with_api.request_json.LoginRequestJSON
import com.example.stunting.database.with_api.request_json.RegisterRequestJSON
import com.example.stunting.database.with_api.response.AddingMessageResponse
import com.example.stunting.database.with_api.response.AddingUserGroupResponse
import com.example.stunting.database.with_api.response.DeleteUserByIdResponse
import com.example.stunting.database.with_api.response.GetAllMessagesResponse
import com.example.stunting.database.with_api.response.GetAllUserGroupResponse
import com.example.stunting.database.with_api.response.GetAllUsersResponse
import com.example.stunting.database.with_api.response.LoginResponse
import com.example.stunting.database.with_api.response.RegisterResponse
import com.example.stunting.database.with_api.response.UpdateUserProfileByIdResponse
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
    fun getAllMessages(): Call<GetAllMessagesResponse>

    @POST("user_profile/{user_id}/group/{group_id}/notification")
    suspend fun addMessage(
        @Path("user_id") userId: Int,
        @Path("group_id") groupId: Int,
        @Body addingMessageRequestJSON: AddingMessageRequestJSON
    ): Response<AddingMessageResponse>

    @GET("user_profiles/groups")
    fun getAllUserGroup(): Call<GetAllUserGroupResponse>

    @POST("user_profile/{user_id}/group")
    suspend fun addUserGroup(
        @Path("user_id") userId: Int,
        @Body addingUserGroupRequestJSON: AddingUserGroupRequestJSON
    ): Response<AddingUserGroupResponse>

    @DELETE("user/{id}")
    suspend fun deleteUserById(
        @Path("id") id: Int
    ): Response<DeleteUserByIdResponse>

    @Multipart
    @PUT("user_profile/{user_id}")
    suspend fun updateUserProfileById(
        @Path("user_id") userId: Int,
        @Part("dataJsonString") dataJsonString: RequestBody?,
        @Part gambarProfile: MultipartBody.Part?,
        @Part gambarBanner: MultipartBody.Part?
    ): Response<UpdateUserProfileByIdResponse>

    @GET("users")
    fun getAllUsers(): Call<GetAllUsersResponse>

    @POST("register")
    suspend fun register(
        @Body requestBody: RegisterRequestJSON
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body requesttBody: LoginRequestJSON
    ): Response<LoginResponse>
}