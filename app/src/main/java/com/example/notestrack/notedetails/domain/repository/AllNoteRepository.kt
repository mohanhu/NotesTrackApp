package com.example.notestrack.notedetails.domain.repository

import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.notedetails.data.model.NotesData
import kotlinx.coroutines.flow.Flow

interface AllNoteRepository {

    suspend fun fetchNotesByMenuId(menuId: Long): Flow<List<NotesData>>

    suspend fun fetchNotesWhereEqualToDate(dateInMs: Long,menuId: Long): Flow<List<NotesData>>

    suspend fun fetchNotesAll(): Flow<List<NotesData>>

    suspend fun fetchCategoryMenuId(menuId: Long): NotesHomeMenuData

    suspend fun updatePinStatus(pinStatus:Boolean,notesId:Long)

    suspend fun deleteNotesId(notesId: Long)

}