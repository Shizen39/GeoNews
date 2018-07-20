package com.example.giorgio.geonews.Activities.ListArticles

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.giorgio.geonews.Activities.MapActivity.MapsActivity.Companion.COUNTRY_KEY
import com.example.giorgio.geonews.Activities.MapActivity.MapsActivity.Companion.QUERIES_KEY
import com.example.giorgio.geonews.Networking.CheckNetworking
import com.example.giorgio.geonews.Networking.Networking
import com.example.giorgio.geonews.R
import kotlinx.android.synthetic.main.activity_list_articles.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by giorgio on 25/06/18.
 * Activity that takes selected country & queries and call fetchArticles
 * -> ListArticlesActivity -> Networking.fetchArticles (end-point, queries)
 * (*) this -> fetchArticles.onResponse() -> RV_Adapter.RecyclerViewAdapter.onBindViewHolder()
 * GeoNews
 */

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

    /** OnCreate func */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_articles)
        onWindowFocusChanged(true)

        /* Creates a vertical Layout Manager */
        RV_news.layoutManager= LinearLayoutManager(this)        // Adapter added by calling fetchArticles.onResponse()

        /* Get intent values from maps activity */
        val e=intent.extras
        val country= e.getString(COUNTRY_KEY)
        val queries= e.getString(QUERIES_KEY)
        title = Locale("", country).displayCountry + if(queries!=null) " - $queries" else " - top news"

        /* Fetch articles and add a new adapter in RV_news */
        fetchArt(country, queries)                                      // (*) this -> fetchArticles.onResponse() -> RV_Adapter.RecyclerViewAdapter

        /* Sets up a SwipeRefreshLayout.OnRefreshListener invoked when the user performs a swipe-to-refresh gesture. */
        val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefreshListArticles)
        mSwipeRefreshLayout.setOnRefreshListener({
            /* Fetch articles and add a new adapter in RV_news */
            fetchArt(country, queries)                                  // fetchArticles
            mSwipeRefreshLayout.isRefreshing = false
        })
    }

    /**
     *  Fetch articles and add a new adapter in RV_news
     */
    private fun fetchArt(country: String, queries: String?){
        if(CheckNetworking.isNetworkAvailable(this))
            if(queries=="" || queries==null)
                Networking.fetchArticles(this, getString(R.string.headlines), "country=$country&pageSize=100&")
            else{
                val df = SimpleDateFormat("yyyy-MM-dd", Locale("", country))
                val cal = Calendar.getInstance()
                cal.add(Calendar.DATE, -1)                      // get from yesterday news
                val date = df.format(cal.time)                          // set date from when to get news
                val language= getLang(country)                          // Get language based on country
                Networking.fetchArticles(this, getString(R.string.everything), "language=$language&q=+$queries&from=$date&sortBy=publishedAt&sortBy=relevancy&pageSize=50&")
            }
        else Toast.makeText(this, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()
    }

    /**
     * Get language from country, end-point everything works only with language, not country
     */
    private fun getLang(country: String): String? {
        val all = Locale.getAvailableLocales()
        for (locale in all) {
            if (locale.toString() == country) {
                return locale.language
            }
        }
        return "en"
    }
}
