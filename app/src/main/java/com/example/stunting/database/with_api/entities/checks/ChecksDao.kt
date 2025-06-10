package com.example.stunting.database.with_api.entities.checks

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stunting.database.with_api.entities.user_group.UserGroupRelation

@Dao
interface ChecksDao {
    @Query("""
        SELECT 
            -- Branch
            b.id_branch AS b_id_branch,
            b.nama_cabang AS b_nama_cabang,
            b.no_tlp AS b_no_tlp,
            b.alamat AS b_alamat,
            b.created_at AS b_created_at,
            b.updated_at AS b_updated_at,
            
            -- UserProfile
            up.id_user_profile AS up_id_user_profile,
            up.user_id AS up_user_id,
            up.branch_id AS up_branch_id,
            up.nama AS up_nama,
            up.nik AS up_nik,
            up.jenis_kelamin AS up_jenis_kelamin,
            up.tgl_lahir AS up_tgl_lahir,
            up.umur AS up_umur,
            up.alamat AS up_alamat,
            up.gambar_profile AS up_gambar_profile,
            up.gambar_banner AS up_gambar_banner,
            up.created_at AS up_created_at,
            up.updated_at AS up_updated_at,
    
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
            upp.updated_at AS upp_updated_at,
            
            -- ChildrenPatient
            cp.id_children_patient AS cp_id_children_patient,
            cp.user_patient_id AS cp_user_patient_id,
            cp.nama_anak AS cp_nama_anak,
            cp.nik_anak AS cp_nik_anak,
            cp.jenis_kelamin_anak AS cp_jenis_kelamin_anak,
            cp.tgl_lahir_anak AS cp_tgl_lahir_anak,
            cp.umur_anak AS cp_umur_anak,
            cp.created_at AS cp_created_at,
            cp.updated_at AS cp_updated_at,
            
            -- CategoryService
            cs.id_category_service AS cs_id_category_service,
            cs.nama_layanan AS cs_nama_layanan,
            cs.created_at AS cs_created_at,
            cs.updated_at AS cs_updated_at,
            
            -- Checks
            c.id_pemeriksaan AS c_id_pemeriksaan,
            c.user_id AS c_user_id,
            c.user_patient_id AS c_user_patient_id,
            c.children_patient_id AS c_children_patient_id,
            c.category_service_id AS c_category_service_id,
            c.tgl_pemeriksaan AS c_tgl_pemeriksaan,
            c.created_at AS c_created_at,
            c.updated_at AS c_updated_at
    
        FROM checks c
        INNER JOIN branch b ON b.id_branch = upp.branch_id
        INNER JOIN user_profile up ON up.user_id = c.user_id
        INNER JOIN user_profile_patient upp ON upp.user_patient_id = c.user_patient_id
        INNER JOIN children_patient cp ON cp.id_children_patient = c.children_patient_id
        INNER JOIN category_service cs ON cs.id_category_service = c.category_service_id
        WHERE c.user_patient_id = :userPatientId AND c.category_service_id = :categoryServiceId
    """)
    fun getChecksRelationByUserPatientIdCategoryServiceId(userPatientId: Int, categoryServiceId: Int): LiveData<List<ChecksRelation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChecks(checks: List<ChecksEntity>)
}