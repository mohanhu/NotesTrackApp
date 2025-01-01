package com.example.notestrack.addmenu.domain.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.utils.network.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoryPagingSource(
    private val addCategoryRepository: AddCategoryRepository,
    private val dataStorePreference: DataStorePreference
):PagingSource<Int,Photo>() {

    private var searchQuery = "notes"

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStorePreference.userData.map { it.cardBackGround }.collectLatest {
                searchQuery = it
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val page = params.key?:1
            var list = listOf<Photo>()

            when(val result = addCategoryRepository.getSearchCardImage(searchQuery.ifEmpty {  "notes" } , page = page)){
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success ->{
                    list = (result.data.photos.map { it.toPhoto() }?: listOf())
                }
            }
            LoadResult.Page(
                data =  list.toList(),
                nextKey = if (list.isEmpty()) null else page+1,
                prevKey = if (page==1) null else page-1
            )
        }
        catch (e:Exception){
            LoadResult.Error(e)
        }
    }
}