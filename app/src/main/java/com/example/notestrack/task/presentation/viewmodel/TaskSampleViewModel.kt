package com.example.notestrack.task.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.home.presentation.viewmodel.LoadState
import com.example.notestrack.task.domain.model.request.TaskRequest
import com.example.notestrack.task.domain.repository.TaskRepository
import com.example.notestrack.utils.network.Result
import com.example.notestrack.utils.paging.LoadType
import com.example.notestrack.utils.paging.PagedRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskSampleViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskSampleUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        TaskSampleUiState()
    )

    private val _scrollToTopSignal = MutableSharedFlow<Boolean>(
        replay = 0
    )

    private var pageNumber:Long = 1L

    private val taskFlow: MutableSharedFlow<List<Photo>> =
        MutableSharedFlow(
            replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    val scrollToTopSignal = _scrollToTopSignal.asSharedFlow()

    private val _uiEvent = MutableSharedFlow<TaskUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val accept: (TaskUiAction) -> Unit

    init {
        accept = ::onUiAction

        val taskItem : Flow<List<TaskUiModel>> =
            taskFlow.map { task->
                if (task.isNotEmpty()){
                    task.map {
                        TaskUiModel.Item(projects = it, selected = false, selectable = false)
                    }
                }else{
                    listOf(TaskUiModel.PlaceHolder(title = "", description = ""))
                }
            }

        val loadState = _uiState.map { it.loadState }

        combine(
            taskItem,
            loadState,
            ::Pair
        ).onEach{(taskItem,load)->
            println("uiState.map  collect $load")
            val newModel = if(load==LoadState.LOAD){
                taskItem.plus(TaskUiModel.Footer(load))
                taskItem.plus(TaskUiModel.Spacer)
            }
            else{
                taskItem.plus(TaskUiModel.Spacer)
            }
            _uiState.update {
                it.copy(taskUiModel = newModel)
            }
        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        refreshTask()
    }

    private fun refreshTask(loadMore:Boolean = false) {
        if (loadMore){
            refreshAll(loadType = LoadType.APPEND)
        }
        else{
            refreshAll()
        }
    }

    private fun refreshAll(loadType: LoadType =LoadType.REFRESH) {

        val pagedRequest:PagedRequest<Long> = if (loadType==LoadType.REFRESH){
            pageNumber = 1L
            PagedRequest.create(loadType = LoadType.REFRESH, pageNumber ,10)
        }
        else{
            PagedRequest.create(loadType = LoadType.APPEND,++pageNumber,10)
        }

        val request = TaskRequest("Nature",pagedRequest)
        viewModelScope.launch(Dispatchers.IO){

            _uiState.update { it.copy(loadState = LoadState.LOAD) }

            when(val result = taskRepository.searchPhotos(request)){
                is Result.Error -> {
                    _uiState.update { it.copy(loadState = LoadState.EMPTY) }
                }
                is Result.Loading -> {}
                is Result.Success -> {

                    pageNumber = result.data.nextKey?:1

                    val nextKey = pagedRequest.key

                   val new = if (nextKey!=1L){
                       _uiState.update { it.copy(loadState = LoadState.LOAD) }
                       taskFlow.replayCache.firstOrNull()?.let { cache->
                           cache.toMutableList().also {
                               it.addAll(result.data.data)
                           }
                       }?:result.data.data
                   }
                    else{
                       _uiState.update { it.copy(loadState = LoadState.LOAD) }
                        result.data.data
                   }
                    taskFlow.emit(new)
                }
            }
        }
    }

    private fun onUiAction(taskUiAction: TaskUiAction) {

    }

    fun updateImageAll() = viewModelScope.launch(Dispatchers.IO){
        (taskFlow.replayCache.firstOrNull()?.map { state->
            state.copy(src = state.src.copy(original = "https://cdn.pixabay.com/photo/2024/01/31/18/39/vibrant-8544591_1280.png"))
        }?: emptyList()).also {
            taskFlow.emit(it)
        }
    }
}

sealed interface TaskUiAction {

}

sealed interface TaskUiEvent {

}

data class TaskSampleUiState(
    val loadState: LoadState = LoadState.IDLE,
    val taskUiModel: List<TaskUiModel> = emptyList(),
)

interface TaskUiModel {
    data class Header(val title: String) : TaskUiModel
    data class Item(val projects:Photo ,val selectable: Boolean, val selected: Boolean) : TaskUiModel
    data class PlaceHolder(val title: String, val description: String?) : TaskUiModel
    data class Footer(val loadState: LoadState) : TaskUiModel
    data object Spacer : TaskUiModel
}

//Ex:
//val list = listOf(
//    Photo(mediaType = MediaType.IMAGE),
//    Photo(mediaType = MediaType.VIDEO),
//    Photo(mediaType = MediaType.DOC),
//)
//fun List<Photo>.toTaskUiModel2() : List<TaskUiModel2>  {
//   return if (isEmpty()){
//       listOf( TaskUiModel2.LoadState)
//   }
//    else{
//       map { p ->
//           when(p.mediaType){
//               MediaType.IMAGE -> TaskUiModel2.Image(photo = p)
//               MediaType.VIDEO ->  TaskUiModel2.Video(photo = p)
//               MediaType.DOC ->  TaskUiModel2.Doc(photo = p)
//               else->{
//                   TaskUiModel2.Placeholder("Empty")
//               }
//           }
//       }
//   }
//}
//
//interface TaskUiModel2 {
//    data class Header(val title:String , val aby:Any):TaskUiModel2
//    data class Image(val photo:Photo): TaskUiModel2
//    data class Video(val photo:Photo): TaskUiModel2
//    data class Doc(val photo:Photo): TaskUiModel2
//    data class Placeholder(val message:String):TaskUiModel2
//    data object LoadState : TaskUiModel2
//}
