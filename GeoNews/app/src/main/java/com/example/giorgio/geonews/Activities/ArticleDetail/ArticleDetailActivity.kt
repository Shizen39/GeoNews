package com.example.giorgio.geonews.Activities.ArticleDetail

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.giorgio.geonews.Activities.ListArticles.adapters.CustomViewHolder
import com.example.giorgio.geonews.Networking.CheckNetworking
import com.example.giorgio.geonews.Networking.Commenting
import com.example.giorgio.geonews.R
import kotlinx.android.synthetic.main.activity_detail_webview.*






/**
 * Activity that show the opened article with a webview
 */


class ArticleDetailActivity : AppCompatActivity() {

    //fBack button on actionBar... For webview, in order to go back in history
    override fun onBackPressed() {
        if (WV_article_detail.canGoBack()) {
            WV_article_detail.goBack()
            onWindowFocusChanged(true)
        } else {
            // Otherwise defer to system default behavior.
            super.onBackPressed()
            onWindowFocusChanged(true)
        }
    }

    //Set FullScreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.decorView.systemUiVisibility= (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        else window.decorView.systemUiVisibility=(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }



    //OnCreate func
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_webview)

        //prevent opening in default browser
        WV_article_detail.webViewClient = WebViewClient()

        //add some settings for webpages
        val settings= WV_article_detail.settings
        settings.javaScriptEnabled=false
        settings.loadWithOverviewMode=true
        settings.useWideViewPort=true
        settings.loadsImagesAutomatically=true
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath("")


        //get url from intent of RV_Adapter
        val articleUrl= intent.getStringExtra(CustomViewHolder.ARTICLE_LINK_KEY)

        if(CheckNetworking.isNetworkAvailable(this))
            WV_article_detail.loadUrl(articleUrl) //load webview's url
        else Toast.makeText(this, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()

        //set actionbar title
        title= if (articleUrl.contains("http://")) articleUrl.removePrefix("http://")  else articleUrl.removePrefix("https://")




        //get fragment
        val ft = fragmentManager.beginTransaction()
        val frag= fragmentManager.findFragmentById(R.id.F_comments) as (ArticleCommentFragment)


        //pass url to comment fragment
        frag.articleUrl=articleUrl



        //and hide it
        ft.hide(frag)
        ft.commit()



        //Set the hide/show button for comments
        addShowHideListener(R.id.F_Button, frag, articleUrl)


    }

    private fun addShowHideListener(buttonId: Int, fragment: Fragment, articleUrl:String) {
        val button = findViewById<View>(buttonId) as FloatingActionButton
        button.setOnClickListener {
            val ft = fragmentManager.beginTransaction()
            ft.setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out)
            if (fragment.isHidden) {
                if(CheckNetworking.isNetworkAvailable(this))
                    Commenting.fetchComments(this, articleUrl) //fetch new comments and VIEW them (in fetchComments.onResponse)
                else Toast.makeText(this, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()

                ft.show(fragment)   //show comments
                onWindowFocusChanged(false) //show navbar
                ft.addToBackStack(null) //for navbar back button to hide fragment

                //button.text = "Hide comments"
            } else {
                ft.hide(fragment)   //hide comments
                onWindowFocusChanged(true) //hide navbar

                //button.text = "Show comments"
            }
            ft.commit()
        }
    }



}
