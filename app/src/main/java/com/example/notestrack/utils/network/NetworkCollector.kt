package com.example.notestrack.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NetworkCollector @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkMonitor {

    override val isOnline: Flow<Boolean> = callbackFlow {

        val connectivityManager = context.getSystemService<ConnectivityManager>()

        val callback = object :NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(isConnected())

            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                trySend(isConnected())
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(isConnected())
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(isConnected())
            }
        }

        connectivityManager?.registerNetworkCallback(NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build(),callback)

        awaitClose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }

    override fun isConnected(): Boolean {
        return context.getSystemService<ConnectivityManager>()?.let {connectivityManager->

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                if(nwInfo.type == ConnectivityManager.TYPE_WIFI) return true

                val nw = connectivityManager.activeNetwork
                val active = connectivityManager.getNetworkCapabilities(nw)

                return when{
                    active?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> true
                    active?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> true
                    active?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> true
                    active?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true -> true
                    else -> false
                }
            }
            else{
                 val nw = connectivityManager.activeNetworkInfo?:return false
                nw.isConnected
            }
        }?:false
    }
}



interface NetworkMonitor{
    val isOnline:Flow<Boolean>
    fun isConnected() : Boolean
}