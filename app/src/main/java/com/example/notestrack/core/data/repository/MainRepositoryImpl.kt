package com.example.notestrack.core.data.repository

import android.content.Context
import androidx.room.Transaction
import com.example.notestrack.core.domain.repository.MainRepository
import com.example.notestrack.core.local.NotesDataBase
import com.example.notestrack.core.local.relation.UserWithCategoryRelation
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import com.example.notestrack.utils.convertMsToDateFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val notesDataBase: NotesDataBase
) : MainRepository {

    override suspend fun selectUserDetails(): Flow<List<UserDetailEntity>> {
        return notesDataBase.userDetailDao.getUserDetailsFlow()
    }

    @Transaction
    override suspend fun getUserRelationWithNotesWhereEqualToDate(userId: Long,date:Long): Flow<List<UserWithCategoryRelation>> {
        return notesDataBase.userDetailDao.getUserRelationWithNotes(userId).map { list->
            list.map { cate->
                cate.copy(
                    categoryTableEntity = cate.categoryTableEntity.filter { convertMsToDateFormat(it.categoryTableEntity.date)== convertMsToDateFormat(date) }
                        .sortedByDescending { it.categoryTableEntity.date }
                )
            }
        }
    }

    @Transaction
    override suspend fun getUserRelationWithNotes(userId: Long): Flow<List<UserWithCategoryRelation>> {
        return notesDataBase.userDetailDao.getUserRelationWithNotes(userId).map { list->
            list.map { cate->
                cate.copy(
                    categoryTableEntity = cate.categoryTableEntity.sortedByDescending { it.categoryTableEntity.date }
                )
            }
        }
    }

    override suspend fun insertUserDetails(userDetailEntity: UserDetailEntity) {
        notesDataBase.userDetailDao.insertData(userDetailEntity)
    }

    override suspend fun updateUserNameImage(userName: String, userImage: String, userId: Long) {
        notesDataBase.userDetailDao.setUserDetails(userId, userName, userImage)
    }

    @Transaction
    override suspend fun deleteCategory(menuId: Long) {
        notesDataBase.categoryTableDao.deleteCategory(menuId)
        notesDataBase.notesDao.deleteNotesByCategoryId(menuId)
    }
}