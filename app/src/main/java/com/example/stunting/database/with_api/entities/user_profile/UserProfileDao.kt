package com.example.stunting.database.with_api.entities.user_profile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserProfileDao {

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
            up.updated_at AS up_updated_at

        FROM user_profile up
        INNER JOIN branch b ON b.id_branch = up.branch_id
        INNER JOIN users u ON u.id_user = up.user_id
        WHERE
            up.user_id = :userId
    """)
    fun getUserProfileRelationByUserIdFromLocal(userId: Int): LiveData<UserProfileWithUserRelation>

    @Query("SELECT * FROM user_profile ORDER BY id_user_profile ASC")
    fun getUserProfilesFromLocal(): LiveData<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfiles(userProfiles: List<UserProfileEntity>)
}