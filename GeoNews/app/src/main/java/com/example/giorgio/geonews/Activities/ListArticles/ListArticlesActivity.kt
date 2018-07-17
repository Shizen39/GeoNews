package com.example.giorgio.geonews.Activities.ListArticles

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.giorgio.geonews.Activities.ListArticles.adapters.RecyclerViewAdapter
import com.example.giorgio.geonews.Activities.MapActivity.MapsActivity.Companion.COUNTRY_KEY
import com.example.giorgio.geonews.Activities.MapActivity.MapsActivity.Companion.QUERIES_KEY
import com.example.giorgio.geonews.Data_utils.News
import com.example.giorgio.geonews.Networking.CheckNetworking
import com.example.giorgio.geonews.Networking.Networking
import com.example.giorgio.geonews.R
import kotlinx.android.synthetic.main.activity_list_articles.*
import java.text.SimpleDateFormat
import java.util.*


class ListArticlesActivity : AppCompatActivity() {

    //Set FullScreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.decorView.systemUiVisibility= (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
    }

    //OnCreate func
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_articles)
        onWindowFocusChanged(true)

        //Creates a vertical Layout Manager and Access the RecyclerView Adapter
        RV_news.layoutManager= LinearLayoutManager(this)
        RV_news.adapter= RecyclerViewAdapter(News(emptyList()))

        //Get selected country
        val e=intent.extras
        val country= e.getString(COUNTRY_KEY)
        val queries= e.getString(QUERIES_KEY)
        title = Locale("", country).displayCountry
        if(CheckNetworking.isNetworkAvailable(this))
            //Fetch articles and add a new adapter in RV_news (in fetchArticles.onBind)
            if(queries=="" || queries==null)
                Networking.fetchArticles(this, getString(R.string.headlines), "country=$country&pageSize=100&")
            else{
                val df = SimpleDateFormat("yyyy-MM-dd", Locale("", country))
                val cal = Calendar.getInstance()
                cal.add(Calendar.DATE, -1) //get from yesterday news
                val date = df.format(cal.time)
                Networking.fetchArticles(this, getString(R.string.everything), "language=$country&q=$queries&from=$date&pageSize=100&")
            }
        else Toast.makeText(this, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()

    }
}
