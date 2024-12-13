package com.example.notestrack.richlib.spanrichlib

import android.widget.EditText
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.example.notestrack.richlib.spanrichlib.RichSpanDownStyle.makeStyleFormat
import com.example.notestrack.richlib.spanrichlib.StyleActionBindClick.editAddLink
import com.example.notestrack.richlib.spanrichlib.StyleActionBindClick.editAddMention
import com.example.notestrack.utils.safeCall
import timber.log.Timber

object BlockExport {

    fun EditText.blockJsonExportToEdit(blockJson: String){
       safeCall {
           val blockKitData = GsonBuilder().disableInnerClassSerialization()
               .disableHtmlEscaping().create()
               .fromJson(blockJson, BlockKitData::class.java)

           var blockKitManage = blockKitData.block?.map { it.toBlockKitManage() }
           var makeString = ""
           blockKitData.block?.mapIndexed { index, head ->
               blockKitManage = blockKitManage?.mapIndexed { i, b ->
                   if (i == index) {
                       b.copy(startIndex = makeString.length, endIndex = makeString.length + b.word.length)
                   } else {
                       b
                   }
               }
               makeString += head.text
           }
           if (makeString.trim().isNotEmpty()){
               setText(makeString, TextView.BufferType.SPANNABLE)
           }
           blockKitManage?.sortedBy { it.startIndex }?.forEach {
               if (it.styleFormat == Styles.MENTION){
                   editAddMention(it.word,it.key.toLong(),it.startIndex,it.endIndex)
               }
               if ( it.styleFormat == Styles.LINK){
                   editAddLink(it.word,it.key,it.startIndex,it.endIndex)
               }
               makeStyleFormat(it.word,it.styleFormat,it.startIndex,it.endIndex)
               println("mentionDataClass.sortedByDescending >>> ${it.styleFormat}")
           }
       }
    }

    fun TextView.blockJsonToExportDataTextView(blockJson: String): List<MentionDataClass> {
        return try {
            Timber.d("TextView.blockJsonToExportDataTextView json >>> $blockJson")
//                val startTime = System.currentTimeMillis()
            val updateFormat = GsonBuilder().disableInnerClassSerialization()
                .disableHtmlEscaping().create().fromJson(blockJson, BlockKitData::class.java)
//                val endTime = System.currentTimeMillis()

//                Timber.d("blockJsonToExportDataTextView Deserialization took ${endTime - startTime} ms")
             var blockList = updateFormat.block?.map { it.toBlockKitManage() }
            var makeString = ""

            updateFormat.block?.mapIndexed { index, blockKitManage ->
                blockList = blockList?.mapIndexed { i, b ->
                    if (i == index) {
                        b.copy(
                            startIndex = makeString.length,
                            endIndex = makeString.length + b.word.length
                        )
                    } else {
                        b
                    }
                }
                makeString += blockKitManage.text
            }
            if (makeString.isNotEmpty()) {
                text = makeString // Update UI on the main thread
            }
            Timber.d("TextView.blockJsonToExportDataTextView >>>> $makeString")
            Timber.d("TextView.blockJsonToExportDataTextView >>>> $blockList")

            blockList?: listOf() // Return the list of MentionDataClass
        } catch (e: Exception) {
            Timber.d("Exception in blockJsonToExportDataTextView: ${e.message}")
            listOf() // Return an empty list on error
    }
}

    fun exportJsonToBlockDataClass(json:String): BlockKitData {
        return try {
            val updateFormat = GsonBuilder().disableInnerClassSerialization()
                .disableHtmlEscaping().create()
                .fromJson(json, BlockKitData::class.java)
            updateFormat
        }
        catch (e:Exception){
            BlockKitData()
        }
    }
}