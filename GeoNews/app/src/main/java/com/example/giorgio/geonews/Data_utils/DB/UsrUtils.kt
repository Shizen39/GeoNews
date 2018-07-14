package com.example.giorgio.geonews.Data_utils.DB

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.provider.Settings


/**
 * Created by giorgio on 10/07/18.
 */


@SuppressLint("HardwareIds")
fun getAndroidID(context: Context): String? {
    return Settings.Secure.getString(context.contentResolver,
            Settings.Secure.ANDROID_ID)
}


fun getColor(android_id: String, url:String): Int {
    var seed:String = Regex("[^a-z0-9]").replace(url, "")
    seed = if(seed.length<=70) //otherwise cannot find color
                seed.substring(7)+android_id
            else //trim
                seed.substring(seed.length-70)+android_id

    var ret= intToARGB(seed.hashCode())
    if(ret.length<6)
        for (i in (ret.length+1)..6)
            ret += "0"
    return try {
        Color.parseColor("#$ret")
    }catch (e: Exception){
        e.printStackTrace()
        Color.parseColor("#666666")
    }
}

private fun intToARGB(i: Int): String {
    return Integer.toHexString(i shr 16 and 0xFF) +
            Integer.toHexString(i shr 8 and 0xFF) +
            Integer.toHexString(i and 0xFF)
}


/*


*/


