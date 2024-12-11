package com.example.notestrack.addmenu.domain.repository

import androidx.paging.PagingData
import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.addmenu.data.model.PhotoDto
import com.example.notestrack.utils.network.Result
import kotlinx.coroutines.flow.Flow

interface AddCategoryRepository {

   suspend fun getSearchCardImage(query:String,page:Int):Result<HomePhotoResponse>

   fun getSearchCard():Flow<PagingData<PhotoDto>>

}