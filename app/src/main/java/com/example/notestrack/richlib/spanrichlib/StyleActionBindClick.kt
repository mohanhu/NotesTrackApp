package com.example.notestrack.richlib.spanrichlib

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.notestrack.R
import com.example.notestrack.utils.safeCall


/** Code optimize
 * will add in after release by
 * Mohan
 * */
object StyleActionBindClick {

    fun EditText.addMention(start:Int,end:Int,mention: String, mentionId: String) {
        val mentionWithSpaces = "@$mention"
        val spannable = SpannableStringBuilder(text)
        spannable.replace(start,end, mentionWithSpaces)

        val clickableSpan = MentionClickableSpan(mentionWithSpaces, mentionId) { name, Id->
            println("MentionClickableSpan >>> $name <$Id")
        }
        println("EditText.onTypeStateChange >>0> $start >>>${spannable.substring(start,start+mentionWithSpaces.length)}")

        clickableSpan.setCurrentStyle(Styles.MENTION)
        spannable.setSpan(clickableSpan, start, start + mentionWithSpaces.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow)), start, start + mentionWithSpaces.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.yellow_green)), start, start + mentionWithSpaces.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val spans = spannable.getSpans(start,start+mentionWithSpaces.length, StyleMakeSpan::class.java)
        spans.forEach {
            spannable.removeSpan(it)
        }
        setText(spannable, TextView.BufferType.SPANNABLE)
        movementMethod = LinkMovementMethod.getInstance()
        insertSafeAt(start + mentionWithSpaces.length," ")
        setSelection(start + mentionWithSpaces.length+1)
    }

    fun EditText.editAddMention(mention: String,mentionId:Long,start:Int,end:Int) {
        val spannable = SpannableStringBuilder(text as Spannable)

        val spans = spannable.getSpans(start,end,Any::class.java)
        spans.forEach {
            spannable.removeSpan(it)
        }

        val clickableSpan = MentionClickableSpan(mention,mentionId.toString()) { name,Id->
            println("MentionClickableSpan >>> $name <$Id")
        }
        clickableSpan.setCurrentStyle(Styles.MENTION)
        spannable.setSpan(clickableSpan, start,
            start + mention.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.yellow_green)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setText(spannable, TextView.BufferType.SPANNABLE)
        movementMethod = LinkMovementMethod.getInstance()
    }

//    fun EditText.showDialogSpanLink(context: Context) {
//
//        val start = selectionStart
//        val end = selectionEnd
//        val longPressText = text.toString().substring(start,end)
//
//        val alertDialogBuilder = AlertDialog.Builder(context, R.style.custom_style_dialog)
//        val dialogView = alertDialogBuilder.create()
//        val customAlertUi = RichTextAddLinkUiBinding.inflate(LayoutInflater.from(context))
//        customAlertUi.evTitle.setText(longPressText)
//        safeCall { customAlertUi.evTitle.setSelection(longPressText.length) } // Not mandatory
//
//        dialogView.let { dialog ->
//            dialog.setView(customAlertUi.root)
//            dialog.setCancelable(true)
//            dialog.setCanceledOnTouchOutside(true)
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            if(!dialog.isShowing){
//                dialog.show()
//            }
//            customAlertUi.evTitle.showKeyboardAndFocus()
//        }
//
//        customAlertUi.doneText.setOnDebounceListener{
//            if (customAlertUi.evTitle.text?.trim().toString().isNotEmpty()){
//                val evLink = customAlertUi.evLink.text.toString().trim().replace("\\s+".toRegex(), "")
//
//                /** Not valid point but need for IOS view Conversion */
//                val formattedLink = if (!evLink.startsWith("http")  && !evLink.contains(".com")) {
//                    "https://$evLink.com"
//                } else {
//                    evLink
//                }
//                addLinkSpanText(title = customAlertUi.evTitle.text.toString().trim(), url = formattedLink)
//                dialogView.dismiss()
//            }
//            else{
//                dialogView.dismiss()
//            }
//        }
//        customAlertUi.negativeText.setOnDebounceListener { dialogView.dismiss() }
//    }

    private fun EditText.addLinkSpanText(title:String, url: String) {
        safeCall {
            val start = selectionStart.takeIf { it != -1 } ?: 0
            val end = selectionEnd.takeIf { it != -1 } ?: 0

            println("MentionClickableSpan >>> start>$start end<$end")

            val spannable = SpannableStringBuilder(text)

            val spanAny = spannable.getSpans(start,end,Any::class.java)
            spanAny.forEach {
                spannable.removeSpan(it)
            }

            // Replace the selected text with the new title
            spannable.replace(start, end, title)

            val clickableSpan = MentionClickableSpan(title, url) { name, Id -> }

            clickableSpan.setCurrentStyle(Styles.LINK)
            spannable.setSpan(clickableSpan, start, start + title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.cornflower_blue)), start, start + title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setText(spannable, TextView.BufferType.SPANNABLE)
            insertSafeAt(start + title.length," ")
            setSelection(start + title.length + 1)
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    fun EditText.editAddLink(name: String,url:String,start:Int,end:Int) {
        safeCall {
            val spannable = SpannableStringBuilder(text as Spannable)
            println("MentionClickableSpan >>>  $start$end")
            println("MentionClickableSpan >>>  <$name>$url>${text.substring(start,end)}")

            val spans = spannable.getSpans(start,end,Any::class.java)
            spans.forEach {
                spannable.removeSpan(it)
            }

            val clickableSpan = MentionClickableSpan(name, url) { _, Id->
            }
            clickableSpan.setCurrentStyle(Styles.LINK)
            spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.cornflower_blue)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setText(spannable, TextView.BufferType.SPANNABLE)
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

}