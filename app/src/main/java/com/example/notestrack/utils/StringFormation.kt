package com.example.notestrack.utils

object StringFormation {

    fun String.capitalise() : String{
        if (isNotEmpty()){
            val firstChar = first().uppercaseChar()
            return firstChar+drop(1)
        }
        return ""
    }
}