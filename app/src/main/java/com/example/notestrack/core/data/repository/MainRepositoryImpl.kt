package com.example.notestrack.core.data.repository

import android.content.Context
import com.example.notestrack.core.domain.repository.MainRepository
import com.example.notestrack.core.local.NotesDataBase
import com.example.notestrack.core.local.relation.UserWithCategoryRelation
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
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

    override suspend fun getUserRelationWithNotes(userId: Long): Flow<List<UserWithCategoryRelation>> {
        return notesDataBase.userDetailDao.getUserRelationWithNotes(userId)
    }

    override suspend fun insertUserDetails(userDetailEntity: UserDetailEntity) {
        notesDataBase.userDetailDao.insertData(userDetailEntity)
    }

    override suspend fun updateUserNameImage(userName: String, userImage: String, userId: Long) {
        notesDataBase.userDetailDao.setUserDetails(userId, userName, userImage)
    }
}