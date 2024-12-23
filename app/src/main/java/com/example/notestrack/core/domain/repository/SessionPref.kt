package com.example.notestrack.core.domain.repository


interface SessionPref {

    val userId: Long

    fun setUserId(userId: Long)

}