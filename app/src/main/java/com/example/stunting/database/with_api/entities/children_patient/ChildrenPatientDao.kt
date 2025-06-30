package com.example.stunting.database.with_api.entities.children_patient

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChildrenPatientDao {

    @Query("""
        UPDATE children_patient 
        SET nama_anak = :namaAnak, nik_anak = :nikAnak, jenis_kelamin_anak = :jenisKelaminAnak, 
            tgl_lahir_anak = :tglLahirAnak, umur_anak = :umurAnak
        WHERE user_patient_id = :userPatientId
        """)
    suspend fun updateChildrenPatientByUserPatientIdFromLocal(
        userPatientId: Int, namaAnak: String, nikAnak: String, jenisKelaminAnak: String,
        tglLahirAnak: String, umurAnak: String
    )

    @Query("SELECT * FROM children_patient WHERE nama_anak = :namaAnak ")
    fun getChildrenPatientByNamaAnak(namaAnak: String): LiveData<ChildrenPatientEntity>

    @Query("SELECT * FROM children_patient WHERE id_children_patient = :childrenPatientId AND user_patient_id = :userPatientId")
    fun getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId: Int, userPatientId: Int): LiveData<ChildrenPatientEntity>

    @Query("SELECT * FROM children_patient WHERE user_patient_id = :userPatientId")
    fun getChildrenPatientByUserPatientIdFromLocal(userPatientId: Int): LiveData<List<ChildrenPatientEntity>>

    @Query("SELECT * FROM children_patient ORDER BY id_children_patient ASC")
    fun getChildrenPatientsFromLocal(): LiveData<List<ChildrenPatientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildrenPatients(childrenPatients: List<ChildrenPatientEntity>)
}