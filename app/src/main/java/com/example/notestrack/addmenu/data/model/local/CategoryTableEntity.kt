package com.example.notestrack.addmenu.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notestrack.home.domain.model.NotesHomeMenuData

@Entity(tableName = CategoryTable.TABLE_NAME)
data class CategoryTableEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = CategoryTable.Column.Category_ID) val categoryId: Long = 0,
    @ColumnInfo(name = CategoryTable.Column.Category_TITLE) val categoryName: String = "",
    @ColumnInfo(name = CategoryTable.Column.Category_IMAGE) val categoryImage: String = "",
    @ColumnInfo(name = CategoryTable.Column.Category_DATE) val date: Long = 0,
    @ColumnInfo(name = CategoryTable.Column.Category_COLOR) val color: String = "",
    @ColumnInfo(name = CategoryTable.Column.USER_ID) val userId: Long = 0,
){
    fun toNotesHomeMenuData(count:Int): NotesHomeMenuData = NotesHomeMenuData(
        menuNotesId = categoryId,
        menuTitle = categoryName,
        thumbNail = categoryImage,
        pickedColor = color,
        userId = userId,
        count = count,
        createdAt = date
    )
}


object CategoryTable {
    const val TABLE_NAME = "CategoryTable"

    object Column {
        const val Category_ID = "Category_ID"
        const val Category_TITLE = "Category_TITLE"
        const val Category_IMAGE = "Category_IMAGE"
        const val Category_DATE = "Category_DATE"
        const val Category_COLOR = "Category_COLOR"
        const val USER_ID = "USER_ID"
    }
}
