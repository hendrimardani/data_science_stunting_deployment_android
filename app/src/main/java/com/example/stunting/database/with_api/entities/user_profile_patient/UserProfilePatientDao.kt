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

    @Query("""
        SELECT
            -- Branch
            b.id_branch AS b_id_branch,
            b.nama_cabang AS b_nama_cabang,
            b.no_tlp AS b_no_tlp,
            b.alamat AS b_alamat,
            b.created_at AS b_created_at,
            b.updated_at AS b_updated_at,
    
            -- Users
            u.id_user AS u_id_user,
            u.email AS u_email,
            u.role AS u_role,
            u.password AS u_password,
            u.created_at AS u_created_at,
            u.updated_at AS u_updated_at,

            -- UserProfilePatient
            upp.id_user_profile_patient AS upp_id_user_profile_patient,
            upp.user_patient_id AS upp_user_patient_id,
            upp.branch_id AS upp_branch_id,
            upp.nama_bumil AS upp_nama_bumil,
            upp.nik_bumil AS upp_nik_bumil,
            upp.tgl_lahir_bumil AS upp_tgl_lahir_bumil,
            upp.umur_bumil AS upp_umur_bumil,
            upp.alamat AS upp_alamat,
            upp.nama_ayah AS upp_nama_ayah,
            upp.gambar_profile AS upp_gambar_profile,
            upp.gambar_banner AS upp_gambar_banner,
            upp.created_at AS upp_created_at,
            upp.updated_at AS upp_updated_at

        FROM user_profile_patient upp 
        INNER JOIN branch b ON b.id_branch = upp.branch_id
        INNER JOIN users u ON u.id_user = upp.user_patient_id
        WHERE
            upp.user_patient_id = :userPatientId
    """)
    fun getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId: Int): LiveData<UserProfilePatientRelation>

    @Query("SELECT * FROM user_profile_patient WHERE nama_bumil = :namaBumil")
    fun getUserProfilePatientByNamaBumil(namaBumil: String): LiveData<UserProfilePatientEntity>

    @Query("SELECT * FROM user_profile_patient ORDER BY id_user_profile_patient ASC")
    fun getUserProfilePatientsFromLocal(): LiveData<List<UserProfilePatientEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProfilePatients(userProfilePatient: List<UserProfilePatientEntity>)
}