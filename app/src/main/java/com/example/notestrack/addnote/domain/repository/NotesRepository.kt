package com.example.notestrack.addnote.domain.repository

import com.example.notestrack.addnote.data.local.entity.NotesTableEntity

interface NotesRepository {

    suspend fun addNotes(notesTableEntity: NotesTableEntity)

}