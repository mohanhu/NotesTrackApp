package com.example.notestrack.core.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NotesSampleObject.TABLE_NAME)
data class NotesSampleEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = NotesSampleObject.COLUMN.SAMPLE_ID) val sampleId: Long = 0,
)

object NotesSampleObject {
    const val TABLE_NAME = "NotesSampleTable"
    object COLUMN {
        const val SAMPLE_ID = "SAMPLE_ID"
    }
}