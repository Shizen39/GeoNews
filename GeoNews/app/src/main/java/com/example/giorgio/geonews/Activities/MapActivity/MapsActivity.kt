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
import android.util.Log
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


class MapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var searchView: SearchView
    var queries: String? =null


    /** INFLATE SEARCH BAR*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
       // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        return true
    }

    //Set FullScreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        Log.w(this.toString(), hasFocus.toString())
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.decorView.systemUiVisibility= (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY     //fullscreen mode
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Set the content to appear under the system bars
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN) // Hide the nav bar and status bar
        else window.decorView.systemUiVisibility=(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    companion object {
        //country to send to articledetailactivity
        val COUNTRY_KEY="COUNTRY"
        val QUERIES_KEY="QUERIES"
    }


    var blue=false
    //OnCreate func
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JodaTimeAndroid.init(this) //init date-time lib
        setContentView(R.layout.activity_maps)
        onWindowFocusChanged(true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /**SearchBar stuffs*/
        searchView = findViewById(R.id.SV_search)
        searchView.setOnClickListener {
            /*handle keyboard showing up*/
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            searchView.isIconified = false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                onWindowFocusChanged(true)
                queries= query
                Toast.makeText(baseContext, "Ricerca news di $queries, seleziona un marker.", Toast.LENGTH_LONG).show()
                if(!blue) fetch(true)
                searchView.queryHint=query
                searchView.isIconified = true
                searchView.clearFocus()
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener {
            if(blue) fetch(false)
            searchView.queryHint="Top news"
            queries=""
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(searchView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)

            false
        }
    }



    private lateinit var countriesISO: ArrayList<String> //list of countries in ISO format

    /**
     * Called when the map is ready
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMapReady(googleMap: GoogleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json))

            if (!success) {
                println("Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            println("Can't find style. Error: ")
        }

        mMap = googleMap
        mMap.setMaxZoomPreference(4.7F)
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = false
        mMap.isTrafficEnabled = false


        countriesISO= arrayListOf("ae", "ar", "at", "au", "be" ,"bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de", "eg", "fr", "gb", "gr","hk", "hu", "id", "ie" ,"il" ,"in", "it", "jp", "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no", "nz" ,"ph", "pl", "pt", "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us" ,"ve", "za")

        fetch(false)

        mMap.setOnMarkerClickListener(this) //click listener on map's markers
    }

    /**
     * Fetch markers
     */

    private fun fetch(boolean: Boolean){
        blue=boolean
        if(CheckNetworking.isNetworkAvailable(baseContext))
            for(i in countriesISO.indices) {
                val deferred= async(context=CommonPool){ //deferred==future in java... val that eventually will have a value
                    getLatLng(i)
                }
                launch (context = UI){ //on the main ui context, it will not block the main thread thanks await() that suspend thread and resume it when deferred will have a result
                    setMarker(deferred.await(), boolean)
                }

                deferred.invokeOnCompletion {
                    deferred.cancel()
                }
            }else Toast.makeText(baseContext, "No internet connection. Check and try again.", Toast.LENGTH_LONG).show()

        mMap.setOnMarkerClickListener(this@MapsActivity)
    }

    /**
     * retrieve Address object from country name, that contains latitude and longitude (and others stuff)
     */
    fun getLatLng(i:Int): Address { //retrieve Address object from country name, that contains latitude and longitude (and others stuff)
        val geocoder = Geocoder(this) //used to retrieve position from location name
        val address= Locale("", countriesISO[i]).displayCountry

        println("Getting $address position....")

        return geocoder.getFromLocationName(address, 1)[0]
    }

    /**
     * set map markers
     */
    fun setMarker(address: Address, search: Boolean){
        if(search)
            mMap.addMarker(MarkerOptions()
                    .position(LatLng(address.latitude,address.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(address.countryCode)).tag = 0 //set the name of  the marker
        else
            mMap.addMarker(MarkerOptions()
                    .position(LatLng(address.latitude,address.longitude)) //Set marker position (by lat and long)
                    .title(address.countryCode)).tag = 0 //set the name of  the marker

    }



    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker:Marker):Boolean {
        onWindowFocusChanged(true)
        // Retrieve the data from the marker.
        var clickCount = marker.tag as Int?
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

        // Check if a click count was set
        if (clickCount != null){
            clickCount += 1 //clicked
            marker.tag = clickCount //bind with marker data
            Toast.makeText(this, "Click another time to view articles of " + marker.title, Toast.LENGTH_SHORT).show()
            //was already clicked, clicked another time
            if(clickCount==2){
                //Send intent whit selected country to articleDetailActivity
                val intent= Intent(this, ListArticlesActivity::class.java)
                val e=Bundle()
                e.putString(QUERIES_KEY, queries)
                e.putString(COUNTRY_KEY, marker.title.toLowerCase())
                intent.putExtras(e)  //send country iso code and eventually queries to fetchnews's query
                this.startActivity(intent)
                clickCount -= 1 //clicked
                marker.tag = clickCount //bind with marker data
                //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

            }
            //Clicked the map
            mMap.setOnMapClickListener {
                onWindowFocusChanged(true)
                clickCount -= 1
                marker.tag = clickCount
               if(blue) marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) else marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
        }
         // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).

        return false
    }

}



