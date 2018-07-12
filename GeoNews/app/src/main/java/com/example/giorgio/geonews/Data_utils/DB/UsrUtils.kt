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
    var seed:String
    seed= Regex("[^a-z0-9]").replace(url, "")
    println(seed)
    if(seed.length<=70) //otherwise cannot find color
        seed= seed.substring(7)+android_id
    else //trim
        seed= seed.substring(seed.length-70)+android_id
    println(seed)
//todo exceptipn
    println(seed.hashCode())
    var ret =intToARGB(seed.hashCode())
    println(ret)

    if(ret.length<6)
        for (i in (ret.length+1)..6) {
            ret += "0"
            println(i)
        }
    println(ret)
    return Color.parseColor("#$ret") //TODO: NON TROVA IL COLORE


}


private fun intToARGB(i: Int): String {
    return Integer.toHexString(i shr 16 and 0xFF) +
            Integer.toHexString(i shr 8 and 0xFF) +
            Integer.toHexString(i and 0xFF)
}


