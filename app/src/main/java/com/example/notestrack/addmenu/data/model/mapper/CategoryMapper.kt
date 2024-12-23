package com.example.notestrack.addmenu.data.model.mapper

import com.example.notestrack.core.local.relation.CategoryWithNotesRelation
import com.example.notestrack.home.domain.model.NotesHomeMenuData

object CategoryMapper {

    fun List<CategoryWithNotesRelation>.toNotesHomeMenuData() : List<NotesHomeMenuData> {
        return map { it.toSingleMapMenuData() }
    }

    fun CategoryWithNotesRelation.toSingleMapMenuData(): NotesHomeMenuData {
        return categoryTableEntity.toNotesHomeMenuData(notesTableEntity.size)
    }
}