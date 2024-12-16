package com.example.notestrack.notedetails.domain.repository

import com.example.notestrack.notedetails.data.model.NotesData

interface AllNoteRepository {

    suspend fun fetchNotesByMenuId(menuId: Long): List<NotesData>

}