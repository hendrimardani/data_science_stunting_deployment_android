package com.example.stunting.database.with_api.entities.children_patient

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ChildrenPatientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChildrenPatients(childrenPatients: List<ChildrenPatientEntity>)
}