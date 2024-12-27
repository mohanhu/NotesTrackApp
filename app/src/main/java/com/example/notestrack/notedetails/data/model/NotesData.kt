package com.example.notestrack.notedetails.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotesData(
    val notesId: Long = 0,
    val notesName: String = "",
    val notesDesc: String = "",
    val notesBlock: String = "",
    val date: Long = 0,
    val categoryId: Long = 0,
    var pinnedStatus:Boolean = false
):Parcelable