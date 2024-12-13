package com.example.notestrack.richlib

interface MarkDownCallBack {

    fun mentionOnClick(mentionId: String){}

    fun urlOnClick(url: String){}

    fun emailOnClick(url:String){}

    fun phoneOnClick(phone:String){}

    fun commonOnClick(patternType: PATTERN_TYPE,clickedString: String)
}
