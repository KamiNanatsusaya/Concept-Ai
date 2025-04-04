package com.manusai.android.sync.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for monitoring network connectivity.
 */
@Singleton
class NetworkMonitor @Inject constructor(
    private val context: Context
) {
    /**
     * Provides a flow that emits network connectivity status changes.
     * 
     * @return Flow of Boolean values indicating if the device is online
     */
    fun isOnline(): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // Initial connectivity check
        val isConnected = isCurrentlyConnected(connectivityManager)
        trySend(isConnected)
        
        // Network callback for monitoring connectivity changes
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            
            override fun onLost(network: Network) {
                trySend(isCurrentlyConnected(connectivityManager))
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasInternet)
            }
        }
        
        // Register the callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // Clean up when the flow is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
    
    /**
     * Checks if the device is currently connected to the internet.
     * 
     * @param connectivityManager The ConnectivityManager instance
     * @return True if connected, false otherwise
     */
    private fun isCurrentlyConnected(connectivityManager: ConnectivityManager): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
