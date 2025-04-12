package com.example.stunting.database.with_api.user_group

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserGroupDao {

    @Query("""
    SELECT 
        -- From user_group
        ug.id_group AS ug_id_group,
        ug.user_id AS ug_user_id,
        ug.role AS ug_role,
        ug.created_by AS ug_created_by,
        ug.created_at AS ug_created_at,
        ug.updated_at AS ug_updated_at,

        -- From user_profile
        up.id_user_profile AS up_id_user_profile,
        up.user_id AS up_user_id,
        up.nama AS up_nama,
        up.nik AS up_nik,
        up.jenis_kelamin AS up_jenis_kelamin,
        up.tgl_lahir AS up_tgl_lahir,
        up.umur AS up_umur,
        up.created_at AS up_created_at,
        up.updated_at AS up_updated_at,

        -- From groups
        g.id_group AS g_id_group,
        g.nama_group AS g_nama_group,
        g.deskripsi AS g_deskripsi,
        g.created_at AS g_created_at,
        g.updated_at AS g_updated_at

    FROM user_group ug
    INNER JOIN user_profile up ON up.user_id = ug.user_id
    INNER JOIN groups g ON g.id_group = ug.id_group
    WHERE ug.user_id = :userId
""")
    fun getUserGroupRelationByUserId(userId: Int): LiveData<List<UserGroupRelation>>

    @Query("SELECT * FROM user_group ORDER BY user_id ASC")
    fun getUserGroup(): LiveData<List<UserGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserGroups(userGroups: List<UserGroupEntity>)
}