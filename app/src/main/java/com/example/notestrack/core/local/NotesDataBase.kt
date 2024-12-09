package com.example.notestrack.core.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
     entities = [NotesSampleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDataBase :  RoomDatabase() {

    abstract val notesSampleDao : NotesSampleDao

}