package com.example.notestrack.task.data.repository

import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.task.domain.model.request.TaskRequest
import com.example.notestrack.task.domain.repository.TaskRemoteDataSource
import com.example.notestrack.task.domain.repository.TaskRepository
import com.example.notestrack.utils.network.NetworkResult
import com.example.notestrack.utils.network.Result
import com.example.notestrack.utils.paging.PagedData
import javax.inject.Inject

class DefaultTaskRepository @Inject
constructor(
    private val remoteDataSource: TaskRemoteDataSource
):TaskRepository{

    override suspend fun searchPhotos(perPage: TaskRequest): Result<PagedData<Long, Photo>> {
        return when(val result = remoteDataSource.searchPhotos(10, page = perPage.pagedRequest.key?.toInt()?:0, perPage.query)){
            is NetworkResult.Loading ->Result.Loading()
            is NetworkResult.Success -> {
                val list = result.data?.photos?.map { it.toPhoto() }?: listOf()

                val nextKey = if (list.size<perPage.pagedRequest.loadSize) null else (perPage.pagedRequest.key)?.plus(1)

                println("DefaultTaskRepository >>> $nextKey >>>$perPage")

                Result.Success(
                    PagedData(
                        data = list,
                        nextKey = nextKey,
                        prevKey = null,
                        totalCount = list.size
                    )
                )
            }
            else -> Result.Error(Exception())
        }
    }
}