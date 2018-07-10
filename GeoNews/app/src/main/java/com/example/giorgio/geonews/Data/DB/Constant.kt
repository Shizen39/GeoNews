package com.example.giorgio.geonews.Data.DB

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings


/**
 * Created by giorgio on 09/07/18.
 */
class Constant {
    // Root Url
    //private static final String BASE_PATH = "http://shapon.website/android/CRUD/";
    private val BASE_PATH = "http://geonews.altervista.org/"

    val INSERT = BASE_PATH + "addComment.php"
    val READ_ALL = BASE_PATH + "getAllComment.php"
    //val UPDATE = BASE_PATH + "updateComment.php"
    val DELETE = BASE_PATH + "deleteComment.php"


    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String? {
        return Settings.Secure.getString(context.contentResolver,
                Settings.Secure.ANDROID_ID)
    }

    //val GET_METHOD = "GET"
    //val POST_METHOD = "POST"

}