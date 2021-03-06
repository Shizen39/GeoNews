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
 * Uses Data_utils.DB.Constants for get php url
 */

/**
 * @CREATE a new comment, posting it on the DB
 * <- ArticleCommentFragment.onClick()
 */
object CreateComment{
    fun createComment(context: Context, commentInput:String, articleUrl: String, android_id: String, usrId:String){
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
        
        client.newCall(request).enqueue(object : Callback {             //can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                (context as Activity).runOnUiThread { Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show() }
            }
            override fun onResponse(call: Call?, response: Response?) {
                Commenting.fetchComments(context, articleUrl)           //Read all comments
            }
        })
    }
}


/**
 * @READ all users id of actual article comments, and return the user id of the user that have posted a comment
 * <- ArticleCommentFragment.GetUsrID()
 */
object RetrieveUsrID {
    /* Fetch all usrID SYNCHRONOUSLY */
    fun getUsrID(articleUrl: String, android_id: String?): String {
        val client = OkHttpClient()
        val url = if (android_id != null) {                                              // Usr has already written, get his usrid
                        Constant().READ_USR + "?url=" + "\"" + articleUrl + "\"" +
                                "&android_id=" + "\"" + android_id + "\""                // usr_id or null
                } else                                                                   // Usr has not already written, get last usrid
                        Constant().READ_MAX_USR + "?url=" + "\"" + articleUrl + "\""     // usr_id

        val req = Request.Builder().url(url).build()

        client.newCall(req).execute().use { response ->                                 // do it synchronously because of id needed
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = response?.body()?.string()                                       // json body response

            return if (!body.equals("{\"Usr\":[]}        ") && !body.equals("{\"Usr\":[{\"usr\":null}]}        \n")) {
                val gson = GsonBuilder().create()
                val usr = gson.fromJson(body, UsrID::class.java)                        // Bind models and json fields
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

/**
 * @DELETE a usr comment
 * <- ArticleCommentFragment.onClick()
 */
object DeleteComment{
    fun deleteComment(context: Activity, articleUrl: String, id: String) {
        val formBody= FormBody.Builder()
                .add("url", articleUrl)
                .add("id", id)
                .build()

        val client= OkHttpClient()
        val request= Request.Builder()
                .url(Constant().DELETE)
                .post(formBody)
                .build()

        client.newCall(request).enqueue(object : Callback {             // can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                context.runOnUiThread { Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show() }
            }
            override fun onResponse(call: Call?, response: Response?) {
                Commenting.fetchComments(context, articleUrl)           // Read all comments
            }
        })
    }
}

/**
 * @UPDATE a usr comment
 * <- fetchComments.onResponse().adapter.onItemClick()
 */
object UpdateComment{
    fun updateComment(context: Context, newComment: String, id: String, articleUrl: String) {
        val formBody= FormBody.Builder()
                .add("newcomment", newComment)
                .add("id", id)
                .build()

        val client= OkHttpClient()
        val request= Request.Builder()
                .url(Constant().UPDATE)
                .post(formBody)
                .build()

        client.newCall(request).enqueue(object : Callback {             // can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                (context as Activity).runOnUiThread { Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show() }
            }
            override fun onResponse(call: Call?, response: Response?) {
                Commenting.fetchComments(context, articleUrl)           //Read all comments
            }
        })
    }
}


