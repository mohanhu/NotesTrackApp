package com.example.notestrack.addmenu.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource.LoadResult
import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.addmenu.data.model.local.CategoryTableEntity
import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.addmenu.domain.repository.AddCategoryRepository
import com.example.notestrack.utils.network.BadApiRequestException
import com.example.notestrack.utils.network.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCategoryRepositoryTest : AddCategoryRepository{

    private val homeList = mutableListOf<HomePhotoResponse>()

    private val observeHome = MutableLiveData<List<HomePhotoResponse>>(homeList)

    private var shouldReturnError = false

    private fun setShouldReturnError(value:Boolean){
        shouldReturnError = value
    }

    override suspend fun getSearchCardImage(query: String, page: Int): Result<HomePhotoResponse> {

        return if (shouldReturnError){
            Result.Error(BadApiRequestException(""))
        }
        else{
            Result.Success(HomePhotoResponse(

            ))
        }
    }

    override fun getSearchCard(): Flow<PagingData<Photo>> {
        return flow { LoadResult.Page(
            data = homeList.first().photos.map { it.toPhoto() },
            nextKey = 2,
            prevKey = null
        ) }
    }

    override suspend fun insertMenuCategory(categoryTableEntity: CategoryTableEntity) {
        val entity = mutableListOf<CategoryTableEntity>().also {
            it.add(categoryTableEntity)
        }
    }
}