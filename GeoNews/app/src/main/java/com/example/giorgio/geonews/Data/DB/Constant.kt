package com.example.giorgio.geonews.Data.DB


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
    val READ_MAX_USR= BASE_PATH + "getMaxUsr.php"
    val READ_USR= BASE_PATH + "getUsr.php"

    var usr_id:String?=null


    //val GET_METHOD = "GET"
    //val POST_METHOD = "POST"

}