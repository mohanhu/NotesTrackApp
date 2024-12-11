package com.example.notestrack.home.domain.model

import java.time.Instant

data class NotesHomeMenuData(
    val menuNotesId: Long = Instant.now().toEpochMilli(),
    val thumbNail: String = "https://projectsly.com/images/blog/best-task-management-system.png?v=1670514978795773617",
    val menuTitle: String = "",
    val pickedColor: String = "#800080"
)