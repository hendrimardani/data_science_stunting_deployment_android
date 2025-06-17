package com.example.stunting.database.with_api.entities.checks

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChecksDao {

    @Query("""
        SELECT
            -- RemajaPutri
            cs.id_child_service AS cs_id_child_service,
            cs.pemeriksaan_id AS cs_pemeriksaan_id,
            cs.tinggi_cm AS cs_tinggi_cm,
            cs.hasil_pemeriksaan AS cs_hasil_pemeriksaan,
            cs.created_at AS cs_created_at,
            cs.updated_at AS cs_updated_at,
            
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
            cats.id_category_service AS cats_id_category_service,
            cats.nama_layanan AS cats_nama_layanan,
            cats.created_at AS cats_created_at,
            cats.updated_at AS cats_updated_at,
            
            -- Checks
            c.id_pemeriksaan AS c_id_pemeriksaan,
            c.user_id AS c_user_id,
            c.user_patient_id AS c_user_patient_id,
            c.children_patient_id AS c_children_patient_id,
            c.category_service_id AS c_category_service_id,
            c.tgl_pemeriksaan AS c_tgl_pemeriksaan,
            c.catatan AS c_catatan,
            c.created_at AS c_created_at,
            c.updated_at AS c_updated_at
    
        FROM checks c
        INNER JOIN child_service cs ON cs.pemeriksaan_id = c.id_pemeriksaan
        INNER JOIN pregnant_mom_service pms ON pms.pemeriksaan_id = c.id_pemeriksaan
        INNER JOIN branch b ON b.id_branch = upp.branch_id
        INNER JOIN user_profile up ON up.user_id = c.user_id
        INNER JOIN user_profile_patient upp ON upp.user_patient_id = c.user_patient_id
        INNER JOIN children_patient cp ON cp.id_children_patient = c.children_patient_id
        INNER JOIN category_service cats ON cats.id_category_service = c.category_service_id
        WHERE
            c.user_patient_id = :userPatientId 
            AND c.category_service_id = :categoryServiceId
            AND upp.nama_bumil LIKE '%' || :searchQuery || '%'
            OR upp.nik_bumil LIKE '%' || :searchQuery || '%'
            OR up.nama LIKE '%' || :searchQuery || '%'
            OR b.nama_cabang LIKE '%' || :searchQuery || '%'
            OR c.catatan LIKE '%' || :searchQuery || '%'
    """)
    fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearchRemajaPutri(
        userPatientId: Int, categoryServiceId: Int, searchQuery: String
    ): LiveData<List<ChecksRelation>>

    @Query("""
        SELECT
            -- ChildService
            cs.id_child_service AS cs_id_child_service,
            cs.pemeriksaan_id AS cs_pemeriksaan_id,
            cs.tinggi_cm AS cs_tinggi_cm,
            cs.hasil_pemeriksaan AS cs_hasil_pemeriksaan,
            cs.created_at AS cs_created_at,
            cs.updated_at AS cs_updated_at,
            
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
            cats.id_category_service AS cats_id_category_service,
            cats.nama_layanan AS cats_nama_layanan,
            cats.created_at AS cats_created_at,
            cats.updated_at AS cats_updated_at,
            
            -- Checks
            c.id_pemeriksaan AS c_id_pemeriksaan,
            c.user_id AS c_user_id,
            c.user_patient_id AS c_user_patient_id,
            c.children_patient_id AS c_children_patient_id,
            c.category_service_id AS c_category_service_id,
            c.tgl_pemeriksaan AS c_tgl_pemeriksaan,
            c.catatan AS c_catatan,
            c.created_at AS c_created_at,
            c.updated_at AS c_updated_at
    
        FROM checks c
        INNER JOIN child_service cs ON cs.pemeriksaan_id = c.id_pemeriksaan
        INNER JOIN pregnant_mom_service pms ON pms.pemeriksaan_id = c.id_pemeriksaan
        INNER JOIN branch b ON b.id_branch = upp.branch_id
        INNER JOIN user_profile up ON up.user_id = c.user_id
        INNER JOIN user_profile_patient upp ON upp.user_patient_id = c.user_patient_id
        INNER JOIN children_patient cp ON cp.id_children_patient = c.children_patient_id
        INNER JOIN category_service cats ON cats.id_category_service = c.category_service_id
        WHERE
            c.user_patient_id = :userPatientId 
            AND c.category_service_id = :categoryServiceId
            AND upp.nama_bumil LIKE '%' || :searchQuery || '%'
            OR upp.nik_bumil LIKE '%' || :searchQuery || '%'
            OR up.nama LIKE '%' || :searchQuery || '%'
            OR b.nama_cabang LIKE '%' || :searchQuery || '%'
            OR c.catatan LIKE '%' || :searchQuery || '%'
    """)
    fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearchAnak(
        userPatientId: Int, categoryServiceId: Int, searchQuery: String
    ): LiveData<List<ChecksRelation>>

    @Query("""
        SELECT
            strftime('%Y', tgl_pemeriksaan) as year,
            strftime('%m', tgl_pemeriksaan) as month,
            COUNT(id_pemeriksaan) as count
        FROM checks
        GROUP BY year, month
        ORDER BY year ASC, month ASC  -- Diubah ke ASC agar urutan waktu benar dari kiri ke kanan
    """)
    fun getTransactionCountByMonth(): LiveData<List<MonthlyTransactionCount>>

    @Query("""
        SELECT
            -- PregnantMomService
            pms.id_pregnant_mom_service AS pms_id_pregnant_mom_service,
            pms.pemeriksaan_id AS pms_pemeriksaan_id,
            pms.hari_pertama_haid_terakhir AS pms_hari_pertama_haid_terakhir,
            pms.tgl_perkiraan_lahir AS pms_tgl_perkiraan_lahir,
            pms.umur_kehamilan AS pms_umur_kehamilan,
            pms.status_gizi_kesehatan AS pms_status_gizi_kesehatan,
            pms.created_at AS pms_created_at,
            pms.updated_at AS pms_updated_at,
            
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
            cats.id_category_service AS cats_id_category_service,
            cats.nama_layanan AS cats_nama_layanan,
            cats.created_at AS cats_created_at,
            cats.updated_at AS cats_updated_at,
            
            -- Checks
            c.id_pemeriksaan AS c_id_pemeriksaan,
            c.user_id AS c_user_id,
            c.user_patient_id AS c_user_patient_id,
            c.children_patient_id AS c_children_patient_id,
            c.category_service_id AS c_category_service_id,
            c.tgl_pemeriksaan AS c_tgl_pemeriksaan,
            c.catatan AS c_catatan,
            c.created_at AS c_created_at,
            c.updated_at AS c_updated_at
    
        FROM checks c
        INNER JOIN pregnant_mom_service pms ON pms.pemeriksaan_id = c.id_pemeriksaan
        INNER JOIN branch b ON b.id_branch = upp.branch_id
        INNER JOIN user_profile up ON up.user_id = c.user_id
        INNER JOIN user_profile_patient upp ON upp.user_patient_id = c.user_patient_id
        INNER JOIN children_patient cp ON cp.id_children_patient = c.children_patient_id
        INNER JOIN category_service cats ON cats.id_category_service = c.category_service_id
        WHERE
            c.user_patient_id = :userPatientId 
            AND c.category_service_id = :categoryServiceId
            AND upp.nama_bumil LIKE '%' || :searchQuery || '%'
            OR upp.nik_bumil LIKE '%' || :searchQuery || '%'
            OR up.nama LIKE '%' || :searchQuery || '%'
            OR b.nama_cabang LIKE '%' || :searchQuery || '%'
            OR c.catatan LIKE '%' || :searchQuery || '%'
    """)
    fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(
        userPatientId: Int, categoryServiceId: Int, searchQuery: String
    ): LiveData<List<ChecksRelation>>

    @Query("""
        SELECT
            -- PregnantMomService
            pms.id_pregnant_mom_service AS pms_id_pregnant_mom_service,
            pms.pemeriksaan_id AS pms_pemeriksaan_id,
            pms.hari_pertama_haid_terakhir AS pms_hari_pertama_haid_terakhir,
            pms.tgl_perkiraan_lahir AS pms_tgl_perkiraan_lahir,
            pms.umur_kehamilan AS pms_umur_kehamilan,
            pms.status_gizi_kesehatan AS pms_status_gizi_kesehatan,
            pms.created_at AS pms_created_at,
            pms.updated_at AS pms_updated_at,

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
            cats.id_category_service AS cats_id_category_service,
            cats.nama_layanan AS cats_nama_layanan,
            cats.created_at AS cats_created_at,
            cats.updated_at AS cats_updated_at,

            -- Checks
            c.id_pemeriksaan AS c_id_pemeriksaan,
            c.user_id AS c_user_id,
            c.user_patient_id AS c_user_patient_id,
            c.children_patient_id AS c_children_patient_id,
            c.category_service_id AS c_category_service_id,
            c.tgl_pemeriksaan AS c_tgl_pemeriksaan,
            c.catatan AS c_catatan,
            c.created_at AS c_created_at,
            c.updated_at AS c_updated_at

        FROM checks c
        INNER JOIN pregnant_mom_service pms ON pms.pemeriksaan_id = c.id_pemeriksaan
        INNER JOIN branch b ON b.id_branch = upp.branch_id
        INNER JOIN user_profile up ON up.user_id = c.user_id
        INNER JOIN user_profile_patient upp ON upp.user_patient_id = c.user_patient_id
        INNER JOIN children_patient cp ON cp.id_children_patient = c.children_patient_id
        INNER JOIN category_service cats ON cats.id_category_service = c.category_service_id
        WHERE
            c.user_id = :userId
            AND c.category_service_id = :categoryServiceId
    """)
    fun getChecksRelationByUserIdCategoryServiceId(userId: Int, categoryServiceId: Int): LiveData<List<ChecksRelation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChecks(checks: List<ChecksEntity>)
}