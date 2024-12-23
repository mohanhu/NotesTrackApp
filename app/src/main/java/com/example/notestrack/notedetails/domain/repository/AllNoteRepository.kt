package com.example.notestrack.notedetails.domain.repository

import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.notedetails.data.model.NotesData

interface AllNoteRepository {

    suspend fun fetchNotesByMenuId(menuId: Long): List<NotesData>

    suspend fun fetchCategoryMenuId(menuId: Long): NotesHomeMenuData

}