package com.example.notestrack.core.service

import retrofit2.Response
import retrofit2.http.POST

interface ApiService {

    @POST("")
    fun getImageDetails(): Response<SimpleResponse>

}

