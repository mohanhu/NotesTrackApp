package com.example.notestrack.utils.network

import java.net.HttpURLConnection

sealed class NetworkResult<T> (
    val data: T? = null,
    open val message:String = "",
    open val code : Int = 0,
){

    class Loading<T> : NetworkResult<T>()

    class Success<T>(
        data: T,
        override val code: Int = HttpURLConnection.HTTP_OK
    ): NetworkResult<T>(data = data)

    class Error<T>(
        val exception: Throwable,
        override val message: String,
        override val code: Int = HttpURLConnection.HTTP_INTERNAL_ERROR
    ): NetworkResult<T>(code = code, message = exception.message.toString())
}


sealed class Result<T> {
    class Loading<T>: Result<T>()
    class Success<T>(val data: T): Result<T>()
    class Error<T>(val exception: Throwable): Result<T>()
}