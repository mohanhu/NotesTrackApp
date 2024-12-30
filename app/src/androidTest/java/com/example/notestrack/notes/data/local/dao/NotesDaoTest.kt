package com.example.notestrack.notes.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.notestrack.addnote.data.local.dao.NotesDao
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity
import com.example.notestrack.core.local.NotesDataBase
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NotesDaoTest {

    @Inject
    @Named("db_testing")
    lateinit var notesDataBase: NotesDataBase

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("Notes_Dao")
    lateinit var notesDao: NotesDao


    @Before
    fun setUp(){
        hiltRule.inject()
//        notesDao = notesDataBase.notesDao
    }

    @After
    fun tearDown(){
        notesDataBase.close()
    }

    @Test
    fun insertNotesTest() = runTest {
        val notes = NotesTableEntity(notesId = 1,
            categoryId = 2, notesName = "test",
            notesDesc = "w",
            notesBlock = "",
            pinnedStatus = false,
            date = 0,
            )
        notesDao.insertNotes(notes)

        val result = notesDao.getNotes().first()
        assertThat(result).contains(notes)
    }

    @Test
    fun deleteNotesTest() = runTest {
        val notes = NotesTableEntity(notesId = 1,
            categoryId = 2, notesName = "test",
            notesDesc = "w",
            notesBlock = "",
            pinnedStatus = false,
            date = 0,
        )
        notesDao.insertNotes(notes)
        notesDao.deleteNotesId(notes.notesId)

        val result = notesDao.getNotes().first()
        assertThat(result).doesNotContain(notes)
    }
}