package com.example.notestrack.core.service

import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(ApiServiceConstants.SEARCH_IMAGE_END)
    suspend fun searchPhotos(@Query("per_page") perPage:Int, @Query("page") page:Int, @Query("query") query:String) : Response<HomePhotoResponse>

}

object ApiServiceConstants{
    const val SEARCH_IMAGE_END = "search"
}