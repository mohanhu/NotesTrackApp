package com.example.notestrack.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notestrack.addmenu.data.model.local.CategoryTableDao
import com.example.notestrack.addmenu.data.model.local.CategoryTableEntity
import com.example.notestrack.addnote.data.local.dao.NotesDao
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity
import com.example.notestrack.profile.data.local.dao.UserDetailDao
import com.example.notestrack.profile.data.local.entity.UserDetailEntity

@Database(
     entities = [
         NotesSampleEntity::class,
     UserDetailEntity::class,
     CategoryTableEntity::class,
     NotesTableEntity::class,
                ],
    version = 3,
    exportSchema = false
)
abstract class NotesDataBase :  RoomDatabase() {

    abstract val notesSampleDao : NotesSampleDao

    abstract val userDetailDao: UserDetailDao

    abstract val categoryTableDao : CategoryTableDao

    abstract val notesDao : NotesDao

}