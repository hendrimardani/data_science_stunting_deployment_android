package com.example.stunting.database.with_api.entities.category_service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface CategoryServiceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategoryServices(categoryServices: List<CategoryServiceEntity>)
}