package com.example.notestrack.task.data.repository

import com.example.notestrack.addmenu.data.model.HomePhotoResponse
import com.example.notestrack.core.service.ApiService
import com.example.notestrack.task.domain.repository.TaskRemoteDataSource
import com.example.notestrack.utils.network.BaseRemoteDataSource
import com.example.notestrack.utils.network.NetworkMonitor
import com.example.notestrack.utils.network.NetworkResult
import javax.inject.Inject

class DefaultTaskRemoteDataSource @Inject constructor(
    private val apiService: ApiService,networkMonitor: NetworkMonitor
) : TaskRemoteDataSource ,BaseRemoteDataSource(networkMonitor) {

    override suspend fun searchPhotos(
        perPage: Int,
        page: Int,
        query: String
    ): NetworkResult<HomePhotoResponse> {
        return safeApiCall { apiService.searchPhotos(perPage, page, query) }
    }

}