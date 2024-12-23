package com.example.notestrack.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ViewExtentions {

    fun View.makeVisible(){
        visibility = View.VISIBLE
    }

    fun View.makeInVisible(){
        visibility = View.INVISIBLE
    }

    fun View.makeGone(){
        visibility = View.GONE
    }


    private val Context.inputManager get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    fun EditText.showKeyBoard() {
        post {
            requestFocus()
            context.inputManager.showSoftInput(this,InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun EditText.hideKeyBoard() {
        post {
            requestFocus()
            context.inputManager.hideSoftInputFromWindow(windowToken,0)
        }
    }

}

fun safeCall(action:()->Unit){
    try {
        action.invoke()
    }
    catch (e:Exception){

    }
}

fun convertMsToDateFormat(millis:Long):String{
    val instant = Instant.ofEpochMilli(millis)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return dateTime.format(DateTimeFormatter.ofPattern("EEE dd-MMMM-yyyy"))
}