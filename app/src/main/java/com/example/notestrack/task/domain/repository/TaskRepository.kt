package com.example.notestrack.task.domain.repository

import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.task.domain.model.request.TaskRequest
import com.example.notestrack.utils.network.Result
import com.example.notestrack.utils.paging.PagedData

interface TaskRepository {

    suspend fun searchPhotos(
        perPage: TaskRequest,
    ): Result<PagedData<Long,Photo>>
}