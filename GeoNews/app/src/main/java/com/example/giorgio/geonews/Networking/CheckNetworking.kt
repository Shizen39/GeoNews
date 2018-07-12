package com.example.giorgio.geonews.Networking

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by giorgio on 12/07/18.
 */
object CheckNetworking {


    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}