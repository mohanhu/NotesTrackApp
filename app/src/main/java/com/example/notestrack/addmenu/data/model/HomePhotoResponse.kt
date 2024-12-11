package com.example.notestrack.addmenu.data.model

import com.google.gson.annotations.SerializedName

data class HomePhotoResponse(
    @SerializedName("next_page") val next_page: String="",
    @SerializedName("page") val page: Int=0,
    @SerializedName("per_page") val per_page: Int=0,
    @SerializedName("photos") val photos: List<PhotoDto> = listOf(),
    @SerializedName("total_results") val total_results: Int=0
)


data class PhotoDto(
    @SerializedName("alt") val alt: String,
    @SerializedName("id") val id: Long,
    @SerializedName("avg_color") val avg_color: String,
    @SerializedName("src") val src: SrcDto
)

data class SrcDto(
    @SerializedName("medium") val original: String,
)