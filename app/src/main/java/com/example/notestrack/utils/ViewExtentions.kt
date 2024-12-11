package com.example.notestrack.utils

import android.view.View

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

}