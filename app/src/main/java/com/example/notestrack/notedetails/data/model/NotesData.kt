package com.example.notestrack.notedetails.data.model

data class NotesData(
    val notesId: Long = 0,
    val notesName: String = "",
    val notesDesc: String = "",
    val notesBlock: String = "",
    val date: String = "",
    val categoryId: Long = 0,
)