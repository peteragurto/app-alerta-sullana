package com.example.alertasullana.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectivityChecker(private val context: Context) {

    fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    interface ConnectivityChangeListener {
        fun onConnectivityChanged(isConnected: Boolean)
    }

    private val listeners = mutableListOf<ConnectivityChangeListener>()

    fun addConnectivityChangeListener(listener: ConnectivityChangeListener) {
        listeners.add(listener)
    }

    fun removeConnectivityChangeListener(listener: ConnectivityChangeListener) {
        listeners.remove(listener)
    }

    // ...

    private fun notifyConnectivityChange(isConnected: Boolean) {
        for (listener in listeners) {
            listener.onConnectivityChanged(isConnected)
        }
    }
}
