package com.example.notestrack.addmenu.data.model

import com.example.notestrack.addmenu.domain.model.HomePhoto
import com.google.gson.annotations.SerializedName

data class HomePhotoResponse(
    @SerializedName("next_page") val next_page: String="",
    @SerializedName("page") val page: Int=0,
    @SerializedName("per_page") val per_page: Int=0,
    @SerializedName("photos") val photos: List<PhotoDto> = listOf(),
    @SerializedName("total_results") val total_results: Int=0
) {
    fun toHomePhoto() = HomePhoto(
        next_page = next_page,
        page = page,
        per_page = per_page,
        photos = photos.map { it.toPhoto() },
        total_results = total_results
    )
}


data class PhotoDto(
    @SerializedName("alt") val alt: String,
    @SerializedName("id") val id: Long,
    @SerializedName("avg_color") val avg_color: String,
    @SerializedName("src") val src: SrcDto
) {
    fun toPhoto() = com.example.notestrack.addmenu.domain.model.Photo(
        alt = alt,
        id = id,
        avg_color = avg_color,
        src = src.toSrc()
    )
}

data class SrcDto(
    @SerializedName("medium") val original: String,
){
    fun toSrc() = com.example.notestrack.addmenu.domain.model.Src(
        original = original
    )
}