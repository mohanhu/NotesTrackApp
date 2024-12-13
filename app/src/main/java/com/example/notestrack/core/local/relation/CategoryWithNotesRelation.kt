package com.example.notestrack.core.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.notestrack.addmenu.data.model.local.CategoryTable
import com.example.notestrack.addmenu.data.model.local.CategoryTableEntity
import com.example.notestrack.addnote.data.local.entity.NotesTable
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity

data class CategoryWithNotesRelation(
    @Embedded
    val categoryTableEntity: CategoryTableEntity,
    @Relation(
        parentColumn = CategoryTable.Column.Category_ID,
        entityColumn = NotesTable.Column.Category_ID,
        entity = NotesTableEntity::class
    )
    val notesTableEntity: List<NotesTableEntity>
)