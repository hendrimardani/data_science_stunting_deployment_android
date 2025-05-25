package com.example.stunting.ui.opening_user_profile_patient_form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.branch.BranchEntity
import com.example.stunting.database.with_api.response.DataBranchesItem
import com.example.stunting.datastore.chatting.ChattingRepository
import kotlinx.coroutines.launch

class OpeningUserProfilePatientFormViewModel(
    private val chattingRepository: ChattingRepository
): ViewModel() {

    // Branch
    private var _getBranchesResult = MutableLiveData<ResultState<List<DataBranchesItem?>>>()
    val getBranchesResult = _getBranchesResult
    val getBranchesFromLocal: LiveData<List<BranchEntity>> = chattingRepository.getBranchesFromLocal()

    init {
        getBranchesFromApi()
    }

    private fun getBranchesFromLocal() = chattingRepository.getBranchesFromLocal()

    fun getBranchesFromApi() {
        viewModelScope.launch {
            _getBranchesResult.value = ResultState.Loading
            _getBranchesResult.value = chattingRepository.getBranchesFromApi()
        }
    }
}