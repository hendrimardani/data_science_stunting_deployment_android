package com.example.stunting.database.with_api.retrofit

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val context: Context,
    private val tokenProvider: () -> String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer ${tokenProvider()}")
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401 || response.code == 403) {
            // Token kadaluarsa atau tidak valid
            redirectToLogin(context)
        }

        return response
    }

    private fun redirectToLogin(context: Context) {
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(context, ContainerMainActivity::class.java)
            intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
