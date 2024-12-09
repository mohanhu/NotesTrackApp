package com.example.notestrack.core.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface NotesSampleDao {

    @Query("SELECT * FROM ${NotesSampleObject.TABLE_NAME}")
    fun selectAllNotes(): List<NotesSampleEntity>

}