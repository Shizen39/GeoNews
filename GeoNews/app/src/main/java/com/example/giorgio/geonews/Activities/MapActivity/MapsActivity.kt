package com.example.giorgio.geonews.Activities.MapActivity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.giorgio.geonews.Activities.ListArticles.ListArticlesActivity
import com.example.giorgio.geonews.Networking.CheckNetworking
import com.example.giorgio.geonews.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import net.danlew.android.joda.JodaTimeAndroid
import java.util.*

/**
 * Created by giorgio on 16/06/18.
 * Activity that consists of a map (google Map) that display available articles with a marker on that location.
 * There's also a search bar (searchView) with which you can search keyword to retrieve relative news
 * MapsActivity -> ListArticlesActivity (countryIso, queries?)
 * GeoNews
 */

class MapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    //Set FullScreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.decorView.systemUiVisibility= (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY     //fullscreen mode
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Set the content to appear under the system bars
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN) // Hide the nav bar and status bar
        else window.decorView.systemUiVisibility=(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    /** Declare variables */
    private lateinit var mMap: GoogleMap
    lateinit var searchView: SearchView
    var queries: String? =null                                  // Eventual queries of searchView that will be passed to LisArticleActivity's Intent
    var blue=false                                              // If a query was typed, than all maps markers will turn to Blue. Set blue=true if so. used
    private lateinit var countriesISO: Array<String>            // List of countries in ISO format

    /** inflate searchView menus */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
       // Inflate the options menu from XML (menu->menu_search)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        return true
    }

    /** Object that conteins key values for intent communications */
    companion object {
        //country to send to articledetailactivity
        val COUNTRY_KEY="COUNTRY"
        val QUERIES_KEY="QUERIES"
    }

    /** OnCreare activity */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JodaTimeAndroid.init(this)                          // init date-time lib
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /* SearchBar stuffs */
        searchView = findViewById(R.id.SV_search)
        searchView.setOnClickListener { /* handle keyboard showing up */
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            searchView.isIconified = false
        }

        /* OnQueryListener */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                onWindowFocusChanged(true)
                queries= query                              // Assign just written query to queries variable, in order to pass it in the Intent
                Toast.makeText(baseContext,
                        "Select a geo-merker to view \"$queries\" news.", Toast.LENGTH_LONG).show()
                if(!blue) fetchMarkers(true)
                searchView.queryHint=query
                searchView.isIconified = true
                searchView.clearFocus()
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        /* Hide keyboard */
        searchView.setOnCloseListener {
            if(blue) fetchMarkers(false)
            searchView.queryHint="Top news"
            queries=""
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(searchView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)

            false
        }
    }

    /**
     * Called when the Map is ready
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMapReady(googleMap: GoogleMap) {
        /** Customise the styling of the base map using a JSON object defined in a raw resource file. */
        try {
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json))
            if (!success) {
                println("Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            println("Can't find style. Error: ")
        }

        /**Init lateinit variables */
        mMap = googleMap
        mMap.setMaxZoomPreference(4.7F)
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = false
        mMap.isTrafficEnabled = false

        countriesISO= arrayOf("ae", "ar", "at", "au", "be" ,"bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de", "eg", "fr", "gb", "gr","hk", "hu", "id", "ie" ,"il" ,"in", "it", "jp", "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no", "nz" ,"ph", "pl", "pt", "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us" ,"ve", "za")

        /** Fetch markers */
        fetchMarkers(false)
    }

    /**
     * Fetch markers
     */
    private fun fetchMarkers(isBlue: Boolean){
        blue=isBlue
        if(CheckNetworking.isNetworkAvailable(baseContext))
            for(i in countriesISO.indices) {                    // Use coroutines async() to return a value from backgroundThread operation. Cannot use launch because it doesn't
                val deferred= async(context=CommonPool){        // async() return deferred. deferred==future in java... val that eventually will have a value
                    getLatLng(i)                                // get address obj from countryIso
                }

                launch (context = UI){                          // on the main ui context, it will not block the main thread thanks await()
                    setMarker(deferred.await(), isBlue)         //  has suspend the thread and will resume it when deferred will have a result
                }

                deferred.invokeOnCompletion {                   // free coroutine on completion
                    deferred.cancel()
                }
            } else Toast.makeText(baseContext, "No internet connection. Check and try again.", Toast.LENGTH_LONG).show()

        mMap.setOnMarkerClickListener(this)                     // click listener on map's markers. Override ->
    }

    /**
     * Retrieve Address object from country name, that contains latitude and longitude (and others stuff)
     */
    private fun getLatLng(i:Int): Address {                                    // for getting latitude and longitude
        val geocoder = Geocoder(this)                                   // used to retrieve position from location name
        val address= Locale("", countriesISO[i]).displayCountry        // get country Name from countryIso
        return geocoder.getFromLocationName(address, 1)[0]            // from country Name return LatLong
    }

    /**
     * Insert map markers
     */
    private fun setMarker(address: Address, search: Boolean){
        if(search)
            mMap.addMarker(MarkerOptions()
                    .position(LatLng(address.latitude,address.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(address.countryCode)).tag = 0                    // set the name of  the marker
        else
            mMap.addMarker(MarkerOptions()
                    .position(LatLng(address.latitude,address.longitude))   // Set marker position (by lat and long)
                    .title(address.countryCode)).tag = 0                    // set name of the marker and tag = 0 that will count how many times a marker get clicks
    }                                                                       // note: tag is one per marker

    /** -> fetchMarkers.mMap.
     * Called when the user clicks a marker.
     * */
    override fun onMarkerClick(marker:Marker):Boolean {
        onWindowFocusChanged(true)

        var clickCount = marker.tag as Int?                                 // Retrieve the data from the marker's tag
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

        if (clickCount != null){                                            // Check if a click count was set
            clickCount += 1                                                 // clicked one time
            marker.tag = clickCount                                         // bind with marker data
            Toast.makeText(this,
                    "Click another time to view articles of "
                            + Locale("", marker.title).displayCountry,
                    Toast.LENGTH_SHORT).show()

            if(clickCount==2){                                                              // was already clicked, clicked another time
                val intent= Intent(this, ListArticlesActivity::class.java)     // Send intent with selected country to articleDetailActivity
                val e=Bundle()
                e.putString(QUERIES_KEY, queries)
                e.putString(COUNTRY_KEY, marker.title.toLowerCase())
                intent.putExtras(e)                                                         // send country iso code and eventually the queries to fetchnews's query
                this.startActivity(intent)
                clickCount -= 1                                                             // restore clicked status
                marker.tag = clickCount                                                     // bind with marker data
            }
            mMap.setOnMapClickListener {                                                    // Clicked the map, not a marker
                onWindowFocusChanged(true)
                clickCount -= 1
                marker.tag = clickCount
               if(blue) marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
               else marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
        }
        /* Return false: we have not consumed the event and we wish for the default behavior to occur */
        return false
    }

}



