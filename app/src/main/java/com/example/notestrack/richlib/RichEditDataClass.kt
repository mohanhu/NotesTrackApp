package com.example.notestrack.richlib

import com.example.notestrack.R
import java.time.Instant

data class RichEditDataClass(
    val richEditId:String = Instant.now().toString(),
    val moreOptionImage:Int = 0,
    var isSelect:Boolean = false,
    val richType: RichTypeEnum = RichTypeEnum.BOLD
)
object Rich {
    fun generateRichStyleData(): List<RichEditDataClass> {
        return listOf(
            RichEditDataClass(moreOptionImage = R.drawable.bold_rich, richType = RichTypeEnum.BOLD),
            RichEditDataClass(moreOptionImage = R.drawable.italic_rich, richType = RichTypeEnum.ITALIC),
            RichEditDataClass(moreOptionImage = R.drawable.under_split_style, richType = RichTypeEnum.STRIKE),
            RichEditDataClass(moreOptionImage = R.drawable.add_link_logo, richType = RichTypeEnum.ADD_LINK),
            RichEditDataClass(moreOptionImage = R.drawable.ic_bullet_list, richType = RichTypeEnum.LIST_BULLET),
            RichEditDataClass(moreOptionImage = R.drawable.ic_number_list, richType = RichTypeEnum.LIST_NUMBER),
            RichEditDataClass(moreOptionImage = R.drawable.rich_under_line, richType = RichTypeEnum.UNDER_LINE),
//            RichEditDataClass(moreOptionImage = R.drawable.align_left_rich, richType = RichTypeEnum.ALIGN_LEFT),
//            RichEditDataClass(moreOptionImage = R.drawable.align_center_rich, richType = RichTypeEnum.ALIGN_CENTER),
//            RichEditDataClass(moreOptionImage = R.drawable.align_right_rich, richType = RichTypeEnum.ALIGN_RIGHT),
//            RichEditDataClass(moreOptionImage = R.drawable.code_rich_edit, richType = RichTypeEnum.CODE_SYMBOL),
//            RichEditDataClass(moreOptionImage = R.drawable.code_block, richType = RichTypeEnum.CODE_BLOCK),
        )
    }
}

enum class RichTypeEnum{
    NORMAL,
    BOLD,
    ITALIC,
    STRIKE,
    ADD_LINK,
    UNDER_LINE,
    ALIGN_LEFT,
    ALIGN_RIGHT,
    ALIGN_CENTER,
    LIST_BULLET,
    LIST_NUMBER,


    CODE_SYMBOL,
    CODE_BLOCK,
    NONE
}