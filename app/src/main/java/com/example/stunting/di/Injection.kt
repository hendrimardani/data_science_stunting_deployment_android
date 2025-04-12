package com.example.stunting.di

import android.content.Context
import com.example.stunting.database.with_api.ChattingDatabase
import com.example.stunting.database.with_api.retrofit.ApiConfig
import com.example.stunting.datastore.chatting.ChattingRepository
import com.example.stunting.datastore.chatting.UserPreference
import com.example.stunting.datastore.chatting.dataStore
import com.example.stunting.utils.AppExecutors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideRepository(context: Context): ChattingRepository {
        val chattingDatabase = ChattingDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(context, user.token)
        val appExecutors = AppExecutors()
        return ChattingRepository(chattingDatabase, apiService, pref, appExecutors)
    }
}