package com.example.giorgio.geonews.Networking

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.giorgio.geonews.Activities.ArticleDetail.adapters.RecyclerViewAdapter
import com.example.giorgio.geonews.Data_utils.DB.Constant
import com.example.giorgio.geonews.Data_utils.Social
import com.example.giorgio.geonews.Data_utils.UsrComment
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_comments.*
import okhttp3.*
import java.io.IOException


/**
 * Created by giorgio on 09/07/18.
 */

object Commenting{
    /**
     * Fetch all comments
     */
    fun fetchComments(context: Context, articleUrl:String){
        val url = Constant().READ_ALL+"?url="+"\""+articleUrl+"\""
        val client = OkHttpClient()
        val req = Request.Builder().url(url).build()

        client.newCall(req).enqueue(object : Callback {  // cannot use .execute() in the UI thread
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string() //json body response

                val social:Social
                if (!body.equals("{\"comments\":[]}")) {
                    //Bind models and json fields
                    val gson = GsonBuilder().create()
                    social = gson.fromJson(body, Social::class.java) //from json to java obj
                }
                else{   //Comment placeholder
                    val comment= listOf(UsrComment("0","Nothing to show", "http://www.nope.it", "nope", " ", " "))
                    social= Social(comment)
                }

                //Send obj to the adapter in a background thread
                (context as Activity).runOnUiThread {  //= RecyclerViewAdapter(social)
                    context.RV_comments.adapter = RecyclerViewAdapter(social,  object : RecyclerViewAdapter.OnItemLongClickListener {
                        override fun onItemClick(item: UsrComment) {
                            DeleteComment.deleteComment(context, item.url, item.comment, item.android_id)//MakeNetworkRequestAsyncTask().execute(item.url, item.comment, item.android_id).get()
                        }

                    })

                }
            }
            override fun onFailure(call: Call?, e: IOException?) {
                println("Nop")
                Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show()
            }
        })
    }
}


