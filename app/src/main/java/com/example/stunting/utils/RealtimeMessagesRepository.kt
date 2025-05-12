package com.example.stunting.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stunting.datastore.chatting.ChattingRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class RealtimeMessagesRepository(private val chattingRepository: ChattingRepository) {


    companion object {
        private val TAG = RealtimeMessagesRepository::class.java.simpleName
    }
}
