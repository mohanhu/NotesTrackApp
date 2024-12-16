package com.example.notestrack.addnote.data.reposiotory

import android.content.Context
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity
import com.example.notestrack.addnote.domain.repository.NotesRepository
import com.example.notestrack.core.local.NotesDataBase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotesRepositoryImpl
@Inject constructor(
    @ApplicationContext context: Context,
    private val notesDataBase: NotesDataBase
) : NotesRepository {

    override suspend fun addNotes(notesTableEntity: NotesTableEntity) {
        notesDataBase.notesDao.insertNotes(notesTableEntity)
    }
}