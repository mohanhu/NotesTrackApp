package com.example.notestrack.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.notestrack.richlib.PATTERN_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

fun NavController.redirectBasedOnPattern(context: Activity,clickString: String, pattern: PATTERN_TYPE) {
    when(pattern){
        PATTERN_TYPE.MENTION -> Unit
        PATTERN_TYPE.URL_PATTERN -> redirectToChrome(context,clickString)
        PATTERN_TYPE.PHONE_PATTERN -> Unit
        PATTERN_TYPE.EMAIL_PATTERN -> Unit
    }
}

fun redirectToChrome(context: Activity, urls: String) {

    // Ensure the URL has a proper scheme (http or https)
    val urlWithScheme = if (!urls.startsWith("http://") && !urls.startsWith("https://")) {
        "https://$urls"
    } else {
        urls
    }

    println("callBackTextString >>> $urlWithScheme >> $urls")

    try {
        CoroutineScope(Dispatchers.Main).launch {
            val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlWithScheme))

            // Check if it's a YouTube URL
            if (urlWithScheme.startsWith("https://www.youtube.com/") || urlWithScheme.startsWith("http://www.youtube.com/")) {
                browseIntent.setPackage("com.google.android.youtube")
            }

            // Check if any app can handle the intent
            if (browseIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(browseIntent)
            } else {
                // Handle the case where there is no app to handle the Intent
                Log.e("OpenURL", "No browser app found")
            }
        }
    } catch (e: Exception) {
        Log.e("OpenURL", "Error opening URL", e)
    }
}
