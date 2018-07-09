package com.example.giorgio.geonews.Activities.MapActivity

import android.content.Intent
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.giorgio.geonews.Activities.ListArticles.ListArticlesActivity
import com.example.giorgio.geonews.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*




class MapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //Set FullScreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.decorView.systemUiVisibility= (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY     //fullscreen mode
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Set the content to appear under the system bars
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN) // Hide the nav bar and status bar
    }

    companion object {
        //country to send to articledetailactivity
        val COUNTRY_KEY="COUNTRY"
    }

    //OnCreate func
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        onWindowFocusChanged(true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap.setMaxZoomPreference(5F)
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = false
        mMap.isTrafficEnabled = false


        countriesISO= arrayListOf("ae", "ar", "at", "au", "be" ,"bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de", "eg", "fr", "gb", "gr","hk", "hu", "id", "ie" ,"il" ,"in", "it", "jp", "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no", "nz" ,"ph", "pl", "pt", "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us" ,"ve", "za")


        for(i in countriesISO.indices) {
            val deferred= async(context=CommonPool){ //deferred==future in java... val that eventually will have a value
                getLatLng(i)
            }
            launch (context = UI){ //on the main ui context, it will not block the main thread thanks await() that suspend thread and resume it when deferred will have a result
                setMarker(deferred.await())
            }

            deferred.invokeOnCompletion {
                deferred.cancel()
            }
        }
        mMap.setOnMarkerClickListener(this) //click listener on map's markers
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
    fun setMarker(address: Address){
        mMap.addMarker(MarkerOptions()
                .position(LatLng(address.latitude,address.longitude)) //Set marker position (by lat and long)
                .title(address.countryCode)).tag = 0 //set the name of  the marker
    }



    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker:Marker):Boolean {
        // Retrieve the data from the marker.
        var clickCount = marker.tag as Int?

        // Check if a click count was set
        if (clickCount != null){
            clickCount += 1 //clicked
            marker.tag = clickCount //bind with marker data
            Toast.makeText(this, "Click another time to view articles of " + marker.title, Toast.LENGTH_SHORT).show()
            //was already clicked, clicked another time
            if(clickCount==2){
                //Send intent whit selected country to articleDetailActivity
                val intent= Intent(this, ListArticlesActivity::class.java)
                intent.putExtra(COUNTRY_KEY, marker.title.toLowerCase())  //send country iso code to fetchnews's query
                this.startActivity(intent)
            }
            //Clicked the map
            mMap.setOnMapClickListener {
                clickCount -= 1
                marker.tag = clickCount
            }
        }
         // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).

        return false
    }

}

/*6556
            //Use Kotlin Coroutines
            (countriesISO.indices).map {
                launch { //start coroutine
                    country.add(getLatLongFromAddress(  //retrieve Address object from country name, that contains latitude and longitude (and others stuff)
                            Locale("", countriesISO[it]).displayCountry)) //translate ISO country in Country name (ex. it=Italia)
                }
            }.forEach { runBlocking { it.join() }}
            //not parallelizable with coroutines: use uithread
            for (i in country.indices) {
                runOnUiThread{
                    mMap.addMarker(MarkerOptions()
                        .position(LatLng(country[i].latitude, country[i].longitude)) //Set marker position (by lat and long)
                        .title(country[i].countryCode)).tag = 0 //set de name of  the marker
                }
            }*/


