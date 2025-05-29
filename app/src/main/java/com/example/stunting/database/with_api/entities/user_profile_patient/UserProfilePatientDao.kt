package com.example.stunting.database.with_api.entities.user_profile_patient

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stunting.ResultState

@Dao
interface UserProfilePatientDao {

    @Transaction
    @Query("SELECT * FROM branch")
    fun getUserProfilePatientWithBranchesRelationFromLocal(): LiveData<List<UserProfilePatientWithBranchesRelation>>

    @Query("SELECT * FROM user_profile_patient ORDER BY id_user_profile_patient ASC")
    fun getUserProfilePatientsFromLocal(): LiveData<List<UserProfilePatientEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProfilePatient(userProfilePatient: List<UserProfilePatientEntity>)
}