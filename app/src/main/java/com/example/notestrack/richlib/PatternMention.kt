package com.example.notestrack.richlib

import android.util.Patterns
import java.util.regex.Pattern

data class PatternString (
    val patternValue : String = "",
    val start:Int = 0,
    val end:Int = 0,
    val color:Int = 0,
    val patternType : PATTERN_TYPE = PATTERN_TYPE.MENTION
)

enum class PATTERN_TYPE(val pattern : String) {
    MENTION(pattern = Pattern.compile("<@(\\d+),[^>]+>").toString()),
    URL_PATTERN(pattern =  Pattern.compile("(^|[\\s.:;?\\-\\]<\\(])" +
                "((https?://|www\\.|pic\\.)[-\\w;/?:@&=+$\\|\\_.!~*\\|'()\\[\\]%#,☺]+[\\w/#](\\(\\))?)" +
                "(?=$|[\\s',\\|\\(\\).:;?\\-\\[\\]>\\)])").toString()),
    PHONE_PATTERN(pattern = Pattern.compile("""^\+91[6-9]\d{9}$""").toString()),
    EMAIL_PATTERN(pattern = Patterns.EMAIL_ADDRESS.toString())
}