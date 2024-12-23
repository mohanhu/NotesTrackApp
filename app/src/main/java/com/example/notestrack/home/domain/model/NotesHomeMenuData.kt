package com.example.notestrack.home.domain.model

import java.time.Instant

data class NotesHomeMenuData(
    val menuNotesId: Long = 0,
    val thumbNail: String = "https://projectsly.com/images/blog/best-task-management-system.png?v=1670514978795773617",
    val menuTitle: String = "",
    val pickedColor: String = "#800080",
    val count:Int=0,
    val userId:Long=0,
    val createdAt: Long=Instant.now().toEpochMilli()
)