package com.example.notestrack.addmenu.data.repository

import android.content.Context
import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.addmenu.domain.repository.AddCategoryDataSource
import com.example.notestrack.core.service.ApiService
import com.example.notestrack.utils.network.BaseRemoteDataSource
import com.example.notestrack.utils.network.NetworkMonitor
import com.example.notestrack.utils.network.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddCategoryDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val networkMonitor: NetworkMonitor,
    private val apiService: ApiService
) : AddCategoryDataSource,BaseRemoteDataSource(networkMonitor) {

    override suspend fun getSearchCardImage(
        query: String,
        page: Int,
        perPage: Int
    ): NetworkResult<HomePhotoResponse>  {
        return safeApiCall { apiService.searchPhotos(
            query = query,
            page = page,
            perPage = perPage
        ) }
    }
}