package com.example.notestrack.addmenu.data.model.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.notestrack.core.local.relation.CategoryWithNotesRelation

@Dao
interface CategoryTableDao {
    @Upsert
    suspend fun insertCategory(categoryTableEntity: CategoryTableEntity)

    @Query("SELECT * FROM ${CategoryTable.TABLE_NAME}")
    suspend fun getAllCategory(): List<CategoryTableEntity>

    @Query("DELETE FROM ${CategoryTable.TABLE_NAME}")
    suspend fun deleteAllCategory()

    @Query("SELECT * FROM ${CategoryTable.TABLE_NAME} WHERE ${CategoryTable.Column.Category_ID}=:categoryId")
    suspend fun selectCategoryDetails(categoryId:Long): CategoryWithNotesRelation

    @Query("SELECT COUNT(*) FROM ${CategoryTable.TABLE_NAME}")
    suspend fun getCategoryCount(): Int

}