package com.example.notestrack.core.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.notestrack.addmenu.data.model.local.CategoryTable
import com.example.notestrack.addmenu.data.model.local.CategoryTableEntity
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import com.example.notestrack.profile.data.local.entity.UserTable

data class UserWithCategoryRelation(
    @Embedded
    val userDetailEntity: UserDetailEntity,

    @Relation(
        parentColumn = UserTable.Column.USER_ID,
        entityColumn = CategoryTable.Column.USER_ID,
        entity = CategoryTableEntity::class
    )
    val categoryTableEntity: List<CategoryWithNotesRelation>
)