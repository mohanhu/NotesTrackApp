package com.example.notestrack.profile.data.repository

import android.content.Context
import com.example.notestrack.core.local.NotesDataBase
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import com.example.notestrack.profile.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val notesDataBase: NotesDataBase
): ProfileRepository{

    override suspend fun getUserDetails(userId: Long): Flow<UserDetailEntity> {
        return notesDataBase.userDetailDao.getUserDetailsFlow().map { entities ->
            entities.first { it.userId == userId }
        }
    }

    override suspend fun updateUserDetails(userId: Long, userName: String, userImage: String) {
        notesDataBase.userDetailDao.setUserDetails(userId, userName, userImage)
    }
}