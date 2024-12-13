package com.example.notestrack.profile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = UserTable.TABLE_NAME)
data class UserDetailEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = UserTable.Column.USER_ID) val userId: Long = 0,
    @ColumnInfo(name = UserTable.Column.USER_NAME) val userName: String = "",
    @ColumnInfo(name = UserTable.Column.USER_IMAGE) val userImage: String = "",
)


object UserTable {
    const val TABLE_NAME = "UserTable"

    object Column {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
        const val USER_IMAGE = "USER_IMAGE"
    }
}
