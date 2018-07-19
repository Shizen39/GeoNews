package com.example.giorgio.geonews.Networking

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by giorgio on 03/07/18.
 * Extension object for checking internet connection
 * GeoNews
 */
object CheckNetworking {
fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}