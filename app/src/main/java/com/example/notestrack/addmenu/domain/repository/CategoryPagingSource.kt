package com.example.notestrack.addmenu.domain.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.notestrack.addmenu.data.model.PhotoDto
import com.example.notestrack.utils.network.Result

class CategoryPagingSource(
    private val addCategoryRepository: AddCategoryRepository
):PagingSource<Int,PhotoDto>() {

    override fun getRefreshKey(state: PagingState<Int, PhotoDto>): Int? {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoDto> {
        return try {

            val page = params.key?:1
            var list = listOf<PhotoDto>()

            when(val result = addCategoryRepository.getSearchCardImage("notes", page = page)){
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success ->{
                    list = (result.data.photos?: listOf())
                }
            }
            LoadResult.Page(
                data =  list,
                nextKey = if (list.isEmpty()) null else page+1,
                prevKey = if (page==1) null else page-1
            )
        }
        catch (e:Exception){
            LoadResult.Error(e)
        }
    }
}