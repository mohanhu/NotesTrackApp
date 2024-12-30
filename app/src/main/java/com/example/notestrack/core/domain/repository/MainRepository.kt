package com.example.notestrack.core.domain.repository

import com.example.notestrack.core.local.relation.UserWithCategoryRelation
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import kotlinx.coroutines.flow.Flow


interface MainRepository {

    suspend fun selectUserDetails(): Flow<List<UserDetailEntity>>

    suspend fun getUserRelationWithNotes(userId: Long): Flow<List<UserWithCategoryRelation>>

    suspend fun getUserRelationWithNotesWhereEqualToDate(userId: Long,date:Long): Flow<List<UserWithCategoryRelation>>

    suspend fun insertUserDetails(userDetailEntity: UserDetailEntity)

    suspend fun updateUserNameImage(userName: String, userImage: String,userId: Long)

    suspend fun deleteCategory(menuId:Long)
}