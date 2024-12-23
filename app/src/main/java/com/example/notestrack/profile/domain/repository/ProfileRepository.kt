package com.example.notestrack.profile.domain.repository

import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getUserDetails(userId:Long): Flow<UserDetailEntity>

    suspend fun updateUserDetails(userId: Long, userName: String, userImage: String)

}