package com.example.stunting.ui.nav_drawer_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataUserProfilesItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class NavDrawerMainViewModel(private val chattingRepository: ChattingRepository) : ViewModel() {
    // UserProfile
    private var _getUserProfilesFromApiResult = MutableLiveData<ResultState<List<DataUserProfilesItem?>>>()
    val getUserProfilesFromApiResult = _getUserProfilesFromApiResult

    init {
        getUserProfilesFromApi()
    }

    private fun getUserProfilesFromApi() {
        viewModelScope.launch {
            _getUserProfilesFromApiResult.value = ResultState.Loading
            _getUserProfilesFromApiResult.value = chattingRepository.getUserProfilesFromApi()
        }
    }

    fun logout() {
        viewModelScope.launch {
            chattingRepository.logout()
        }
    }
}