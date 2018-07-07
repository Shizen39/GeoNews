package com.example.giorgio.geonews.Networking

import android.app.Activity
import android.content.Context
import com.example.giorgio.geonews.Activities.ListArticles.adapters.RecyclerViewAdapter
import com.example.giorgio.geonews.Data.News
import com.example.giorgio.geonews.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_list_articles.*
import okhttp3.*
import java.io.IOException


/**
 * Created by giorgio on 02/07/18.
 * Singleton class that consists of helper methods used for requesting and retrieving
 * article data from the News API
 */

object Networking {
    /**
     * OKHTTP: Queries the articles' dataset and returns a list of Article objects.
     */
    fun fetchArticles(context: Context, end_point: String, queries: String?) {
        println("getting URL...")
        val URL = getUrl(context, end_point, queries)
        println(URL)
        println("Attempting to fetch JSON")

        /*TODO: TRY CATCH? */
        // Performs HTTP request (GET) and return a JSON response.
        val client = OkHttpClient()
        val request = Request.Builder().url(URL).build()

        client.newCall(request).enqueue(object : Callback { //can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                println("Failed to execute request (okhttp)")
            }
            /**
             * GSON: parse json body response's fields, binding them whit Models
             */
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string() //json body response

                //Bind models and json fields
                val gson = GsonBuilder().create()
                val news = gson.fromJson(body, News::class.java) //from json to java obj

                //Send obj to the adapter in a background thread
                (context as Activity).runOnUiThread {context.RV_news.adapter= RecyclerViewAdapter(news) }
            }
        })
    }

    /**
     * Function to form the final URL
     */
    private fun getUrl(context: Context, end_point: String, queries: String?): String {
        return context.getString(R.string.news_api_url) + end_point + "?" + queries + "apiKey=" + context.getString(R.string.news_api_key)
    }
}