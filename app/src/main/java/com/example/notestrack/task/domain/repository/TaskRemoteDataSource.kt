package com.example.notestrack.task.domain.repository

import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.utils.network.NetworkResult

interface TaskRemoteDataSource {

    suspend fun searchPhotos(perPage:Int, page:Int, query:String) : NetworkResult<HomePhotoResponse>

}