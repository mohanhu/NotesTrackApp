package com.example.notestrack.profile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.notestrack.core.local.relation.UserWithCategoryRelation
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import com.example.notestrack.profile.data.local.entity.UserTable
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(userDetailEntity: UserDetailEntity)

    @Query("SELECT * FROM ${UserTable.TABLE_NAME}")
    suspend fun getUserDetails(): List<UserDetailEntity>

    @Query("SELECT * FROM ${UserTable.TABLE_NAME}")
    fun getUserDetailsFlow(): Flow<List<UserDetailEntity>>

    @Transaction
    @Query("SELECT * FROM ${UserTable.TABLE_NAME} WHERE ${UserTable.Column.USER_ID}=:userId")
    fun getUserRelationWithNotes(userId: Long): Flow<List<UserWithCategoryRelation>>

    @Query("DELETE FROM ${UserTable.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("UPDATE ${UserTable.TABLE_NAME} SET ${UserTable.Column.USER_NAME}=:userName , ${UserTable.Column.USER_IMAGE}=:userImage WHERE ${UserTable.Column.USER_ID}=:userId")
    fun setUserDetails(userId: Long, userName: String, userImage: String)

}