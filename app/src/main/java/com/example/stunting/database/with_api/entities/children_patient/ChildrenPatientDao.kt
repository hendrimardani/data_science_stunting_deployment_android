package com.example.stunting.database.with_api.entities.children_patient

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientWithUserRelation

@Dao
interface ChildrenPatientDao {

//    @Transaction
//    @Query("""
//        SELECT * FROM children_patient
//        INNER JOIN user_profile_patient ON user_profile_patient.user_patient_id = children_patient.user_patient_id
//        WHERE user_patient_id = :userPatientId
//    """)
//    fun getChildrenPatientithUserProfileRelationByUserPatientIdFromLocal(userPatientId: Int): LiveData<ChildrenPatientWithUserProfilePatient>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChildrenPatients(childrenPatients: List<ChildrenPatientEntity>)
}