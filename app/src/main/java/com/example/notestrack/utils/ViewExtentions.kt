package com.example.notestrack.utils

import android.content.Context
import android.hardware.input.InputManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.notestrack.utils.ViewExtentions.hideKeyBoard
import com.example.notestrack.utils.ViewExtentions.showKeyBoard

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
            context.inputManager.hideSoftInputFromWindow(windowToken,InputMethodManager.HIDE_IMPLICIT_ONLY)
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