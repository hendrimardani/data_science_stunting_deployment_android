package com.example.stunting.database.with_api.entities.children_patient

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientWithUserRelation

@Dao
interface ChildrenPatientDao {

    @Query("SELECT * FROM children_patient WHERE nama_anak = :namaAnak ")
    fun getChildrenPatientByNamaAnak(namaAnak: String): LiveData<ChildrenPatientEntity>

    @Query("SELECT * FROM children_patient WHERE id_children_patient = :childrenPatientId AND user_patient_id = :userPatientId")
    fun getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId: Int, userPatientId: Int): LiveData<ChildrenPatientEntity>

    @Query("SELECT * FROM children_patient WHERE user_patient_id = :userPatientId")
    fun getChildrenPatientByUserPatientIdFromLocal(userPatientId: Int): LiveData<List<ChildrenPatientEntity>>

    @Query("SELECT * FROM children_patient ORDER BY id_children_patient ASC")
    fun getChildrenPatientsFromLocal(): LiveData<List<ChildrenPatientEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChildrenPatients(childrenPatients: List<ChildrenPatientEntity>)
}