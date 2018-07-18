package com.example.giorgio.geonews.Activities.Splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.giorgio.geonews.Activities.MapActivity.MapsActivity

class SplashActivity : AppCompatActivity() {

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

    //OnCreate func
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onWindowFocusChanged(true)

        val intent= Intent(this, MapsActivity::class.java)
        this.startActivity(intent)
        this.finish()

    }
}
