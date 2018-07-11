package com.example.giorgio.geonews.Networking

import android.app.Activity
import android.content.Context
import com.example.giorgio.geonews.Activities.ArticleDetail.adapters.RecyclerViewAdapter
import com.example.giorgio.geonews.Data.DB.Constant
import com.example.giorgio.geonews.Data.Social
import com.example.giorgio.geonews.Data.UsrComment
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
        println(articleUrl)

        val url = Constant().READ_ALL+"?url="+"\""+articleUrl+"\""
        val client = OkHttpClient()
        val req = Request.Builder().url(url).build()

        println(url)

            client.newCall(req).enqueue(object : Callback {  // cannot use .execute() in the UI thread
                override fun onResponse(call: Call?, response: Response?) {
                    val body = response?.body()?.string() //json body response

                    if (!body.equals("{\"comments\":[]}")) {
                        //Bind models and json fields
                        val gson = GsonBuilder().create()
                        val social = gson.fromJson(body, Social::class.java) //from json to java obj

                        //Send obj to the adapter in a background thread
                        (context as Activity).runOnUiThread { context.RV_comments.adapter = RecyclerViewAdapter(social) }


                    }
                    else{
                        val comment= listOf(UsrComment("0","Nothing to show", "http://www.nope", "nope", " ", " "))
                        val social= Social(comment)
                        (context as Activity).runOnUiThread { context.RV_comments.adapter = RecyclerViewAdapter(social) }
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    println("Nop")
                    println(e.toString())
                }
            })


    }
}


