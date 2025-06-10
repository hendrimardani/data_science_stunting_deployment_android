package com.example.stunting.database.with_api.entities.user_profile_patient

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface UserProfilePatientDao {

    @Update
    suspend fun updateUserProfilePatientByIdFromLocal(userProfilePatient: UserProfilePatientEntity)

    @Transaction
    @Query("""
        SELECT * FROM user_profile_patient
        INNER JOIN users ON users.id_user = user_profile_patient.user_patient_id
        WHERE user_patient_id = :userPatientId
    """)
    fun getUserProfilePatientWithUserRelationByIdFromLocal(userPatientId: Int): LiveData<UserProfilePatientWithUserRelation>

    @Transaction
    @Query("""
        SELECT * FROM user_profile_patient
        INNER JOIN branch ON branch.id_branch = user_profile_patient.branch_id 
        WHERE user_patient_id = :userPatientId
        """)
    fun getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId: Int): LiveData<UserProfilePatientsWithBranchRelation>

    @Query("SELECT * FROM user_profile_patient ORDER BY id_user_profile_patient ASC")
    fun getUserProfilePatientsFromLocal(): LiveData<List<UserProfilePatientEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProfilePatients(userProfilePatient: List<UserProfilePatientEntity>)
}