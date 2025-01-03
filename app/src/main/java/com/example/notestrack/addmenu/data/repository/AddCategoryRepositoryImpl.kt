package com.example.notestrack.addmenu.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.addmenu.data.model.local.CategoryTableEntity
import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.addmenu.domain.repository.AddCategoryDataSource
import com.example.notestrack.addmenu.domain.repository.AddCategoryRepository
import com.example.notestrack.addmenu.domain.repository.CategoryPagingSource
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.local.NotesDataBase
import com.example.notestrack.utils.network.BadApiRequestException
import com.example.notestrack.utils.network.NetworkResult
import com.example.notestrack.utils.network.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCategoryRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val addCategoryDataSource: AddCategoryDataSource,
    private val notesDataBase: NotesDataBase,
    private val dataStorePreference: DataStorePreference
): AddCategoryRepository {

    override suspend fun getSearchCardImage(query: String, page: Int): Result<HomePhotoResponse> {
        return when (val result = addCategoryDataSource.getSearchCardImage(query = query, page = page, perPage = 20)) {
            is NetworkResult.Error -> Result.Error(BadApiRequestException(""))
            is NetworkResult.Loading -> Result.Loading()
            is NetworkResult.Success -> Result.Success(data = result.data ?: HomePhotoResponse())
        }
    }

    override fun getSearchCard(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                CategoryPagingSource(
                    this,
                    dataStorePreference
                )
            }
        ).flow
    }

    override suspend fun insertMenuCategory(categoryTableEntity: CategoryTableEntity) {
        notesDataBase.categoryTableDao.insertCategory(categoryTableEntity)
    }
}
