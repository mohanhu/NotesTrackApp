package com.example.notestrack.task.domain.model.request

import com.example.notestrack.utils.paging.PagedRequest

data class TaskRequest (
    val query:String,
    val pagedRequest: PagedRequest<Long>
)