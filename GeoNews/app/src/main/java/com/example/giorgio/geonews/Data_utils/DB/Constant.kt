package com.example.giorgio.geonews.Data_utils.DB


/**
 * Created by giorgio on 09/07/18.
 */
class Constant {
    private val BASE_PATH = "http://geonews.altervista.org/"

    val INSERT = BASE_PATH + "addComment.php"
    val READ_ALL = BASE_PATH + "getAllComment.php"

    //val UPDATE = BASE_PATH + "updateComment.php"
    val DELETE = BASE_PATH + "deleteComment.php"

    val READ_MAX_USR= BASE_PATH + "getMaxUsr.php"
    val READ_USR= BASE_PATH + "getUsr.php"



}