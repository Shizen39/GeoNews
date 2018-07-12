package com.example.giorgio.geonews.Networking

import android.os.AsyncTask
import com.example.giorgio.geonews.Data_utils.DB.Constant
import com.example.giorgio.geonews.Data_utils.UsrID
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


/**
 * Created by giorgio on 10/07/18.
 */
object Retrieving {

    /**
     * Fetch all usrID
     */
    fun getUsrID(articleUrl: String, android_id: String?): String {
        val client = OkHttpClient()
        val url = if (android_id != null) //Usr has already written, get his usrid
                    Constant().READ_USR + "?url=" + "\"" + articleUrl + "\"" + "&android_id=" + "\"" + android_id + "\"" //usr_id or null
                else                      //Usr has not already written, get last usrid
                    Constant().READ_MAX_USR + "?url=" + "\"" + articleUrl + "\"" //usr_id

        val req = Request.Builder().url(url).build()
        client.newCall(req).execute().use { response -> //do it synchronously because of id needed
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = response?.body()?.string() //json body response

            return if (!body.equals("{\"Usr\":[]}") && !body.equals("{\"Usr\":[{\"usr\":null}]}")) {
                //Bind models and json fields
                val gson = GsonBuilder().create()
                val usr = gson.fromJson(body, UsrID::class.java) //from json to java obj
                usr.Usr[0].usr
            } else ""
        }
    }


    class MakeNetworkRequestAsyncTask: AsyncTask<String, Void, String>() {

        // The system calls this to perform work in a worker thread and
        // delivers it the parameters given to AsyncTask.execute()
        override fun doInBackground(vararg params: String): String {
            return Retrieving.getUsrID(params[0], params[1])
        }

        // The system calls this to perform work in the UI thread and
        // delivers the result from doInBackground() method defined above
        override fun onPostExecute(result: String?) {
        }
    }
}










/*

 */
/*
fun getUsrID(context: Activity,articleUrl: String, android_id: String?) {



        println("GETUSRID")

        val client = OkHttpClient()
        val url = if (android_id != null) //Usr has already written, get his usrid
            Constant().READ_USR + "?url=" + "\"" + articleUrl + "\"" + "&android_id=" + "\"" + android_id + "\"" //usr_id or null
        else //Usr has not already written, get last usrid
            Constant().READ_MAX_USR + "?url=" + "\"" + articleUrl + "\"" //usr_id


        val req = Request.Builder().url(url).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                println("meh")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string() //json body response

                if (!body.equals("{\"Usr\":[]}") && !body.equals("{\"Usr\":[{\"usr\":null}]}")) {
                    //Bind models and json fields
                    val gson = GsonBuilder().create()
                    val usr = gson.fromJson(body, UsrID::class.java) //from json to java obj

                    //Send obj to the adapter in a background thread
                    /*
                        context.activity.runOnUiThread {
                            context.check = usr.Usr[0].usr
                        }*/

                        ret= usr.Usr[0].usr

                } else
                    ret=""
            }
        })




    }

 */


