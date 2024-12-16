package com.example.notestrack.addnote.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.notestrack.addnote.data.local.entity.NotesTable
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity

@Dao
interface NotesDao {

    @Upsert
    suspend fun insertNotes(categoryTableEntity: NotesTableEntity)

    @Query("SELECT * FROM ${NotesTable.TABLE_NAME} WHERE ${NotesTable.Column.Category_ID}=:menuId")
    suspend fun getAllNotes(menuId:Long): List<NotesTableEntity>

    @Query("DELETE FROM ${NotesTable.TABLE_NAME}")
    suspend fun deleteAllNotes()

    @Query("SELECT COUNT(*) FROM ${NotesTable.TABLE_NAME}")
    suspend fun getNotesCount(): Int
}