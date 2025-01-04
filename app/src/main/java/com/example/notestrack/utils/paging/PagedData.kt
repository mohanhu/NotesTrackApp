package com.example.notestrack.utils.paging

data class PagedData<Key: Any, T>(
    val data: List<T>,
    val totalCount: Int,
    val prevKey: Key?,
    val nextKey: Key?
)