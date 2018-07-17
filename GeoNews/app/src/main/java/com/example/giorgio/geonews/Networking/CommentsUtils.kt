package com.example.giorgio.geonews.Networking

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.example.giorgio.geonews.Data_utils.DB.Constant
import com.example.giorgio.geonews.Data_utils.UsrID
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


/**
 * Created by giorgio on 10/07/18.
 * CRUD STUFFS
 */



/**
 * @CREATE a new comment, posting it on the DB
 */
object CreateComment{
    fun post(context: Context, commentInput:String, articleUrl: String, android_id: String, usrId:String){
        val formBody= FormBody.Builder()
                .add("comment", commentInput)
                .add("url", articleUrl)
                .add("android_id", android_id)
                .add("usr", usrId)
                .build()
        val client= OkHttpClient()
        val request= Request.Builder()
                .url(Constant().INSERT)
                .post(formBody)
                .build()

        client.newCall(request).enqueue(object : Callback { //can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                (context as Activity).runOnUiThread { Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show() }
            }
            /**
             * GSON: parse json body response's fields, binding them whit Models
             */
            override fun onResponse(call: Call?, response: Response?) {
                //Read all comments
                Commenting.fetchComments(context, articleUrl)
            }
        })
    }
}


/**
 * @READ all users id of actual article comments, and return the user id of the user that have posted a comment
 */
object RetrieveUsrID {
    //Fetch all usrID SYNCHRONOUSLY
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
            println(body) //TODO: WARNING, BODY IF NULL CHANGES WITH "           \N"

            return if (!body.equals("{\"Usr\":[]}        ") && !body.equals("{\"Usr\":[{\"usr\":null}]}        \n")) {
                //Bind models and json fields
                val gson = GsonBuilder().create()
                val usr = gson.fromJson(body, UsrID::class.java) //from json to java obj
                usr.Usr[0].usr
            } else ""
        }
    }

     // AsyncTask for response handling
    class MakeNetworkRequestAsyncTask: AsyncTask<String, Void, String>() {
        // The system calls this to perform work in a worker thread and
        // delivers it the parameters given to AsyncTask.execute()
        override fun doInBackground(vararg params: String): String {
            return RetrieveUsrID.getUsrID(params[0], params[1])
        }

        // The system calls this to perform work in the UI thread and
        // delivers the result from doInBackground() method defined above
        override fun onPostExecute(result: String?) {
        }
    }
}



object DeleteComment{
    fun deleteComment(context: Activity, articleUrl: String, comment: String, android_id: String) {

        val formBody= FormBody.Builder()
                .add("url", articleUrl)
                .add("comment", comment)
                .add("android_id", android_id)
                .build()

        val client= OkHttpClient()
        val request= Request.Builder()
                .url(Constant().DELETE)
                .post(formBody)
                .build()

        client.newCall(request).enqueue(object : Callback { //can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                context.runOnUiThread { Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show() }
            }
            override fun onResponse(call: Call?, response: Response?) {
                //Read all comments
                Commenting.fetchComments(context, articleUrl)
            }
        })
    }
}

object UpdateComment{
    fun updateComment(context: Context, newComment: String, oldComment: String, articleUrl: String) {
        val formBody= FormBody.Builder()
                .add("newcomment", newComment)
                .add("oldcomment", oldComment)
                .add("url", articleUrl)
                .build()

        val client= OkHttpClient()
        val request= Request.Builder()
                .url(Constant().UPDATE)
                .post(formBody)
                .build()

        client.newCall(request).enqueue(object : Callback { //can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                (context as Activity).runOnUiThread { Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show() }
            }
            override fun onResponse(call: Call?, response: Response?) {
                //Read all comments
                Commenting.fetchComments(context, articleUrl)
            }
        })
    }
}


