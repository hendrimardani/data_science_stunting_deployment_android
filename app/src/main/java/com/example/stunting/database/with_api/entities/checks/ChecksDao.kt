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
            -- user_profile
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
    
            -- children patient
            cp.id_children_patient AS cp_id_children_patient,
            cp.user_patient_id AS cp_user_patient_id,
            cp.nama_anak AS cp_nama_anak,
            cp.nik_anak AS cp_nik_anak,
            cp.jenis_kelamin_anak AS cp_jenis_kelamin_anak,
            cp.tgl_lahir_anak AS cp_tgl_lahir_anak,
            cp.umur_anak AS cp_umur_anak,
            cp.created_at AS cp_created_at,
            cp.updated_at AS cp_updated_at,
            
            -- category service
            cs.id_category_service AS cs_id_category_service,
            cs.nama_layanan AS cs_nama_layanan,
            cs.created_at AS cs_created_at,
            cs.updated_at AS cs_updated_at,
            
            -- checks
            c.user_id AS c_user_id,
            c.children_patient_id AS c_children_patient_id,
            c.category_service_id AS c_category_service_id,
            c.tgl_pemeriksaan AS c_tgl_pemeriksaan,
            c.catatan AS c_catatan,
            c.created_at AS cs_created_at,
            c.updated_at AS cs_updated_at
            
        FROM checks c
        INNER JOIN user_profile up ON up.user_id = c.user_id
        INNER JOIN children_patient cp ON cp.id_children_patient = c.children_patient_id
        INNER JOIN category_service cs ON cs.id_category_service = c.category_service_id
        WHERE c.children_patient_id = :childrenPatientId
    """)
    fun getChecksRelationByChildrenPatientId(childrenPatientId: Int): LiveData<List<ChecksRelation>>

    @Query("SELECT * FROM checks ORDER BY user_id ASC")
    fun getChecks(): LiveData<List<ChecksEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecks(checks: List<ChecksEntity>)
}