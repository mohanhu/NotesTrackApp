package com.example.notestrack.addnote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NotesTable.TABLE_NAME)
data class NotesTableEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = NotesTable.Column.Notes_ID) val notesId: Long = 0,
    @ColumnInfo(name = NotesTable.Column.Notes_TITLE) val notesName: String = "",
    @ColumnInfo(name = NotesTable.Column.Notes_DESC) val notesDesc: String = "",
    @ColumnInfo(name = NotesTable.Column.NOTES_BLOCK) val notesBlock: String = "",
    @ColumnInfo(name = NotesTable.Column.Notes_DATE) val date: String = "",
    @ColumnInfo(name = NotesTable.Column.Category_ID) val categoryId: Long = 0,
)


object NotesTable {
    const val TABLE_NAME = "NotesTable"

    object Column {
        const val Notes_ID = "Notes_ID"
        const val Notes_TITLE = "Notes_TITLE"
        const val Notes_DESC = "Notes_DESC"
        const val Notes_DATE = "Notes_DATE"
        const val NOTES_BLOCK = "NOTES_BLOCK"
        const val Category_ID = "Category_ID"
    }
}
