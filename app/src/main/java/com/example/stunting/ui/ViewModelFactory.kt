package com.example.stunting.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stunting.datastore.chatting.ChattingRepository
import com.example.stunting.di.Injection
import com.example.stunting.ui.anak.AnakViewModel
import com.example.stunting.ui.anak_patient.AnakPatientViewModel
import com.example.stunting.ui.bumil.BumilViewModel
import com.example.stunting.ui.bumil_patient.BumilPatientViewModel
import com.example.stunting.ui.detail_anak_patient.DetailAnakPatientViewModel
import com.example.stunting.ui.login.LoginViewModel
import com.example.stunting.ui.nav_drawer_fragment.NavDrawerMainViewModel
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatientViewModel
import com.example.stunting.ui.nav_drawer_patient_fragment.daftar_anak.NavDaftarAnakPatientViewModel
import com.example.stunting.ui.nav_drawer_patient_fragment.user_profile.NavUserProfilePatientViewModel
import com.example.stunting.ui.opening_user_profile_patient_form.OpeningUserProfilePatientFormViewModel
import com.example.stunting.ui.sign_up.SignUpViewModel
import com.example.stunting.ui.tambah_anak_patient.TambahAnakPatientViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: ChattingRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OpeningUserProfilePatientFormViewModel::class.java) -> {
                OpeningUserProfilePatientFormViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NavDrawerMainActivityPatientViewModel::class.java) -> {
                NavDrawerMainActivityPatientViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NavUserProfilePatientViewModel::class.java) -> {
                NavUserProfilePatientViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NavDaftarAnakPatientViewModel::class.java) -> {
                NavDaftarAnakPatientViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailAnakPatientViewModel::class.java) -> {
                DetailAnakPatientViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TambahAnakPatientViewModel::class.java) -> {
                TambahAnakPatientViewModel(repository) as T
            }


            modelClass.isAssignableFrom(BumilPatientViewModel::class.java) -> {
                BumilPatientViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AnakPatientViewModel::class.java) -> {
                AnakPatientViewModel(repository) as T
            }


            modelClass.isAssignableFrom(NavDrawerMainViewModel::class.java) -> {
                NavDrawerMainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BumilViewModel::class.java) -> {
                BumilViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AnakViewModel::class.java) -> {
                AnakViewModel(repository) as T
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