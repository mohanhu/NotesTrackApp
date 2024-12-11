package com.example.notestrack.utils.network

import androidx.annotation.WorkerThread
import retrofit2.Response

open class BaseRemoteDataSource (
    private val networkMonitor: NetworkMonitor
) {

    @WorkerThread
    suspend fun <T> safeApiCall(apiCall : suspend ()-> Response<T>) : NetworkResult<T> {
        return try {
           if (networkMonitor.isConnected()){
               apiCall.invoke().let {response : Response<T> ->
                   println("BaseRemoteDataSource >>> $response")
                   if (response.isSuccessful) {
                       response.body()?.let {
                           NetworkResult.Success(
                               it,
                               code = response.code()
                           )
                       } ?: error("Success. But no data")
                   }
                   else{
                       NetworkResult.Error(
                           message = "Api trigger failed",
                           exception = BadApiRequestException("Api trigger failed")
                       )
                   }
               }
           }
            else{
               NetworkResult.Error(
                   message = "Api trigger failed",
                   exception = BadApiRequestException("Network not connected")
               )
           }
        }
        catch (e:Exception){
            NetworkResult.Error(message = "Api trigger failed", exception = e)
        }
    }
}

data class BadApiRequestException(val error: String): Exception(error)