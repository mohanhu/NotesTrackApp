package com.example.notestrack.addmenu.domain.repository

import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.utils.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AddCategoryDataSource {

    suspend fun getSearchCardImage(query:String,page:Int,perPage:Int):NetworkResult<HomePhotoResponse>

}