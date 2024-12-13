package com.example.notestrack.addmenu.data.model.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CategoryTableDao {
    @Upsert
    suspend fun insertCategory(categoryTableEntity: CategoryTableEntity)

    @Query("SELECT * FROM ${CategoryTable.TABLE_NAME}")
    suspend fun getAllCategory(): List<CategoryTableEntity>

    @Query("DELETE FROM ${CategoryTable.TABLE_NAME}")
    suspend fun deleteAllCategory()

    @Query("SELECT COUNT(*) FROM ${CategoryTable.TABLE_NAME}")
    suspend fun getCategoryCount(): Int

    @Query("UPDATE ${CategoryTable.TABLE_NAME} SET ${CategoryTable.Column.Category_NOTE_COUNT}=:count WHERE ${CategoryTable.Column.Category_ID}=:id")
    suspend fun updateTaskCount(id: Long,count:Int)

}