package com.example.notestrack.notedetails.data.repository

import android.content.Context
import com.example.notestrack.addmenu.data.model.mapper.CategoryMapper.toSingleMapMenuData
import com.example.notestrack.core.local.NotesDataBase
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.notedetails.data.model.NotesData
import com.example.notestrack.notedetails.domain.repository.AllNoteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AllNoteRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val notesDataBase: NotesDataBase
): AllNoteRepository {

    override suspend fun fetchNotesAll(): Flow<List<NotesData>> {
        return notesDataBase.notesDao.getNotes().map { entities -> entities.map { it.toNotesData() }.sortedByDescending { it.date } }
    }

    override suspend fun fetchNotesByMenuId(menuId: Long): Flow<List<NotesData>> {
        return notesDataBase.notesDao.getAllNotes(menuId).map { entities -> entities.map { it.toNotesData() }.sortedByDescending { it.date } }
    }

    override suspend fun fetchCategoryMenuId(menuId: Long): NotesHomeMenuData {
        return notesDataBase.categoryTableDao.selectCategoryDetails(menuId).toSingleMapMenuData()
    }

    override suspend fun updatePinStatus(pinStatus: Boolean, notesId: Long) {
        notesDataBase.notesDao.updatePinStatus(pinStatus,notesId)
    }

    override suspend fun deleteNotesId(notesId: Long) {
        notesDataBase.notesDao.deleteNotesId(notesId)
    }
}