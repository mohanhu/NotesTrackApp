package com.example.notestrack.addmenu.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSourceFactory
import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.addmenu.data.model.PhotoDto
import com.example.notestrack.addmenu.domain.repository.AddCategoryDataSource
import com.example.notestrack.addmenu.domain.repository.AddCategoryRepository
import com.example.notestrack.addmenu.domain.repository.CategoryPagingSource
import com.example.notestrack.utils.network.BadApiRequestException
import com.example.notestrack.utils.network.NetworkResult
import com.example.notestrack.utils.network.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddCategoryRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val addCategoryDataSource: AddCategoryDataSource
): AddCategoryRepository {

    override suspend fun getSearchCardImage(query: String, page: Int): Result<HomePhotoResponse> {
        return when (val result = addCategoryDataSource.getSearchCardImage(query = query, page = page, perPage = 20)) {
            is NetworkResult.Error -> Result.Error(BadApiRequestException(""))
            is NetworkResult.Loading -> Result.Loading()
            is NetworkResult.Success -> Result.Success(data = result.data ?: HomePhotoResponse())
        }
    }

    override fun getSearchCard(): Flow<PagingData<PhotoDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                CategoryPagingSource(
                    this
                )
            }
        ).flow
    }
}
