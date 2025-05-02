package com.example.stunting.database.with_api.entities.messages

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessagesDao {

    @Query("""
        SELECT
            -- user_profile
            up.id_user_profile AS up_id_user_profile,
            up.user_id AS up_user_id,
            up.nama AS up_nama,
            up.nik AS up_nik,
            up.jenis_kelamin AS up_jenis_kelamin,
            up.tgl_lahir AS up_tgl_lahir,
            up.umur AS up_umur,
            up.alamat AS up_alamat,
            up.created_at AS up_created_at,
            up.updated_at AS up_updated_at,
    
            -- groups
            g.id_group AS g_id_group,
            g.nama_group AS g_nama_group,
            g.deskripsi AS g_deskripsi,
            g.gambar_profile AS g_gambar_profile,
            g.gambar_banner AS g_gambar_banner,
            g.created_at AS g_created_at,
            g.updated_at AS g_updated_at,

            -- notifications
            n.id_notification AS n_id_notification,
            n.is_status AS n_is_status,
            n.created_at AS n_created_at,
            n.updated_at AS n_updated_at,
            
            -- messages
            m.user_id AS m_user_id,
            m.group_id AS m_group_id,
            m.notification_id AS m_notification_id,
            m.isi_pesan AS m_isi_pesan,
            m.created_at AS m_created_at,
            m.updated_at AS m_updated_at

        FROM messages m
        INNER JOIN user_profile up ON up.user_id = m.user_id
        INNER JOIN groups g ON g.id_group = m.group_id
        INNER JOIN notifications n ON n.id_notification = m.notification_id
        WHERE m.group_id = :groupId
    """)
    fun getMessageRelationByGroupId(groupId: Int): LiveData<List<MessagesRelation>>

    @Query("SELECT * FROM messages ORDER BY user_id ASC")
    fun getMessages(): LiveData<List<MessagesEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessages(messagesEntity: List<MessagesEntity>)
}
