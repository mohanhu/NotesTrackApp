package com.example.notestrack.richlib.spanrichlib

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.notestrack.R
import com.example.notestrack.richlib.MarkDownCallBack
import com.example.notestrack.richlib.NeedPatternList
import com.example.notestrack.richlib.PATTERN_TYPE
import com.example.notestrack.richlib.PatternString
import com.example.notestrack.richlib.needPatternList
import com.example.notestrack.utils.safeCall
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin

/**
* Text color conversion based on PATTERN_TYPE
**/

@AndroidEntryPoint
class RichSpanText @JvmOverloads constructor(context: Context, attributeSet: AttributeSet) : AppCompatTextView(context,attributeSet) {

    private lateinit var richSpanCallBack: MarkDownCallBack

    fun getInstance(mark: MarkDownCallBack) {
        richSpanCallBack = mark
    }

    private var spannableStringBuilder : SpannableStringBuilder = SpannableStringBuilder(" ")

    private val markwon = Markwon.builder(context)
        .usePlugin(StrikethroughPlugin.create())
        .build()

    override fun setText(text: CharSequence?, type: BufferType?) { super.setText(text, type) }

    suspend fun startPatternRecognition(
        markDown: Boolean,
        needPatternList: List<NeedPatternList>,
        mentionList: List<MentionDataClass>
    ) {

         /** This only for markdown after length issue soln if better get , will change */
        spannableStringBuilder = if (markDown){
            SpannableStringBuilder(markwon.toMarkdown(text.toString().replace("\n","  \n")+("\u00A0").repeat(50)))
        }
        else{
            SpannableStringBuilder(text)
        }
        spannableStringBuilder = SpannableStringBuilder(spannableStringBuilder)

        val findString = needPatternList(spannableStringBuilder.toString(),needPatternList)
        if(findString.isNotEmpty()) {
            spanStringUpdate(findString){
                spanCustomStyle(mentionList)
            }
        }
        else{
            spanCustomStyle(mentionList)
        }
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = ContextCompat.getColor(context, R.color.transparent)
    }

    private fun spanStringUpdate(
        findString: List<PatternString>,
        mentionFinish : ()->Unit
    ) {
        try {
            findString.toList().asReversed().mapIndexed { _, patternString ->
                var changeName = ""

                val start = patternString.start
                val end = patternString.end

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        when(patternString.patternType){
                            /**
                             * Temporarily get username through json .
                             * */
                            PATTERN_TYPE.MENTION -> {
                                val id = "<@(\\d+),([^>]+)>".toRegex()
                                val value = id.find(patternString.patternValue)?.groupValues
                                println("markDownCallBack.mentionOnClick >>> $value")
                                safeCall {
                                    richSpanCallBack.mentionOnClick(value?.get(2)?:"")
                                    richSpanCallBack.commonOnClick(patternType = PATTERN_TYPE.MENTION, clickedString = value?.get(2)?:"")
                                }
                           }
                            PATTERN_TYPE.URL_PATTERN ->{
                                safeCall {
                                    richSpanCallBack.urlOnClick(patternString.patternValue)
                                    richSpanCallBack.commonOnClick(patternType = PATTERN_TYPE.URL_PATTERN,patternString.patternValue)
                                }
                            }

                            PATTERN_TYPE.PHONE_PATTERN -> {
                                safeCall {
                                    richSpanCallBack.phoneOnClick(patternString.patternValue)
                                    richSpanCallBack.commonOnClick(patternType = PATTERN_TYPE.PHONE_PATTERN,patternString.patternValue)
                                }
                           }
                            PATTERN_TYPE.EMAIL_PATTERN -> {
                                safeCall {
                                    richSpanCallBack.emailOnClick(patternString.patternValue)
                                    richSpanCallBack.commonOnClick(patternType = PATTERN_TYPE.EMAIL_PATTERN,patternString.patternValue)
                                }
                            }
                        }
                    }
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                spannableStringBuilder.apply {
                    when(patternString.patternType){
                        PATTERN_TYPE.MENTION -> {
                            val id = "<@(\\d+),([^>]+)>".toRegex()
                            val value = id.find(patternString.patternValue)?.groupValues
                            val userId = value?.get(1)?:0
                            changeName = value?.get(2)?:""

                            println("spanStringUpdate >>> start>$value>id<$userId>$changeName")
                            spannableStringBuilder = spannableStringBuilder.replace(start,end+1,changeName)
                            setSpan(clickableSpan, start, start + changeName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            setSpan(ForegroundColorSpan(ContextCompat.getColor(context, patternString.color)), start, start + changeName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.yellow)), start, start + changeName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        PATTERN_TYPE.URL_PATTERN -> {
                            setSpan(clickableSpan, start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            setSpan(ForegroundColorSpan(ContextCompat.getColor(context, patternString.color)), start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        PATTERN_TYPE.EMAIL_PATTERN -> {
                            setSpan(clickableSpan, start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            setSpan(ForegroundColorSpan(ContextCompat.getColor(context, patternString.color)), start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        PATTERN_TYPE.PHONE_PATTERN -> {
                            setSpan(clickableSpan, start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            setSpan(ForegroundColorSpan(ContextCompat.getColor(context, patternString.color)), start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
                setText(spannableStringBuilder,BufferType.SPANNABLE)
                spannableStringBuilder = SpannableStringBuilder(spannableStringBuilder)
            }
            mentionFinish.invoke()
        }
        catch (_:Exception){}
    }

    private fun spanCustomStyle(spans: List<MentionDataClass>){
        try {
            spans.sortedBy { it.startIndex }.map {
                spannableStringBuilder.apply {
                    if (it.styleFormat == Styles.MENTION) {
                        val linkSpan = MentionClickableSpan(mentionName = it.word, mentionId = it.key){name,id->
                            richSpanCallBack.mentionOnClick(mentionId = id)
                            richSpanCallBack.commonOnClick(patternType = PATTERN_TYPE.MENTION, clickedString = id)
                        }
                        linkSpan.setCurrentStyle(Styles.MENTION)
                        setSpan(linkSpan,it.startIndex,it.endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow)), it.startIndex, it.endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.yellow_green)), it.startIndex, it.endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if(it.styleFormat == Styles.LINK){
                        val linkSpan = MentionClickableSpan(mentionName = it.word, mentionId = it.key){key,url->
                            richSpanCallBack.urlOnClick(url = url)
                            richSpanCallBack.commonOnClick(patternType = PATTERN_TYPE.URL_PATTERN, clickedString = url)
                        }
                        linkSpan.setCurrentStyle(Styles.LINK)
                        setSpan(linkSpan,it.startIndex,it.endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.cornflower_blue)), it.startIndex, it.endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if ( it.styleFormat == Styles.BOLD) {
                        setSpan(StyleMakeSpan(Styles.BOLD,it.word),it.startIndex,it.endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if ( it.styleFormat == Styles.ITALIC) {
                        setSpan(StyleMakeSpan(Styles.ITALIC,it.word),it.startIndex,it.endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if ( it.styleFormat == Styles.UNDER_LINE) {
                        spannableStringBuilder.setSpan(StyleMakeSpan(Styles.UNDER_LINE,""),it.startIndex,it.endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if ( it.styleFormat == Styles.STRIKE) {
                        spannableStringBuilder.setSpan(StyleMakeSpan(Styles.STRIKE,""),it.startIndex,it.endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                setText(spannableStringBuilder,BufferType.SPANNABLE)
                spannableStringBuilder = SpannableStringBuilder(spannableStringBuilder)
            }
        }
        catch (e:Exception){

        }
    }

}

