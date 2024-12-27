package com.example.notestrack.addnote.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.notestrack.addnote.data.local.entity.NotesTable
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    suspend fun insertNotes(categoryTableEntity: NotesTableEntity)

    @Transaction
    @Query("SELECT * FROM ${NotesTable.TABLE_NAME} WHERE ${NotesTable.Column.Category_ID}=:menuId")
    fun getAllNotes(menuId:Long): Flow<List<NotesTableEntity>>

    @Transaction
    @Query("SELECT * FROM ${NotesTable.TABLE_NAME}")
    fun getNotes(): Flow<List<NotesTableEntity>>

    @Query("DELETE FROM ${NotesTable.TABLE_NAME}")
    suspend fun deleteAllNotes()

    @Query("SELECT COUNT(*) FROM ${NotesTable.TABLE_NAME}")
    suspend fun getNotesCount(): Int

    @Query("UPDATE ${NotesTable.TABLE_NAME} SET ${NotesTable.Column.PINNED_STATUS}=:pinStatus WHERE ${NotesTable.Column.Notes_ID}=:notesId")
    suspend fun updatePinStatus(pinStatus:Boolean,notesId:Long)

    @Query("DELETE FROM ${NotesTable.TABLE_NAME} WHERE ${NotesTable.Column.Category_ID}=:menuId")
    suspend fun deleteNotesByCategoryId(menuId:Long)

    @Query("DELETE FROM ${NotesTable.TABLE_NAME} WHERE ${NotesTable.Column.Notes_ID}=:notesId")
    suspend fun deleteNotesId(notesId:Long)
}