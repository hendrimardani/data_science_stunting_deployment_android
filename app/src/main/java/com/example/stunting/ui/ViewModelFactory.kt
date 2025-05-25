package com.example.stunting.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stunting.datastore.chatting.ChattingRepository
import com.example.stunting.di.Injection
import com.example.stunting.ui.opening_user_profile_patient_form.OpeningUserProfilePatientFormViewModel
import com.example.stunting.ui.sign_up.SignUpViewModel

class ViewModelFactory(private val repository: ChattingRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OpeningUserProfilePatientFormViewModel::class.java) -> {
                OpeningUserProfilePatientFormViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context): ViewModelFactory {
            val repository = Injection.provideRepository(context)
            return ViewModelFactory(repository)
        }
    }
}