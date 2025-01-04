package com.example.notestrack.addmenu.domain.model

import kotlin.random.Random

data class HomePhoto(
    val next_page: String="",
    val page: Int=0,
    val per_page: Int=0,
    val photos: List<Photo> = listOf(),
    val total_results: Int=0
)


data class Photo(
    val id: Long=0,
    val alt: String="",
    val avg_color: String="",
    val src: Src=Src(),
    val mediaType: MediaType = MediaType.ListDummy.getRandomPick()
)


enum class MediaType(val key:Int){
    IMAGE(12),
    VIDEO(13),
    DOC(14),;

    object ListDummy {
        fun getRandomPick() = MediaType.entries.get(Random.nextInt(0,MediaType.entries.size-1))
    }
}

data class Src(
    val original: String = "",
)