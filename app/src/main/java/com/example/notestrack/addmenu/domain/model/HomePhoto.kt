package com.example.notestrack.addmenu.domain.model

data class HomePhoto(
    val next_page: String="",
    val page: Int=0,
    val per_page: Int=0,
    val photos: List<Photo> = listOf(),
    val total_results: Int=0
)


data class Photo(
    val id: Long,
    val alt: String,
    val avg_color: String,
    val src: Src
)

data class Src(
    val original: String,
)