package com.example.giorgio.geonews.Activities.ArticleDetail

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.giorgio.geonews.Data_utils.DB.Constant
import com.example.giorgio.geonews.Data_utils.DB.getAndroidID
import com.example.giorgio.geonews.Data_utils.DB.getColor
import com.example.giorgio.geonews.Networking.CheckNetworking
import com.example.giorgio.geonews.Networking.CreateComment
import com.example.giorgio.geonews.Networking.RetrieveUsrID
import com.example.giorgio.geonews.R
import kotlinx.android.synthetic.main.row_comments.*




/**
 * Created by giorgio on 03/07/18.
 */
class ArticleCommentFragment : Fragment(), View.OnClickListener {

    /**
     * Init variables
     */
    lateinit var commentInput: EditText     //Edit text for input comment
    lateinit var my_img: TextView
    lateinit var c: Constant
    lateinit var android_id: String
    lateinit var articleUrl: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)

        val sendButton: Button = view.findViewById(R.id.send_comment) //init send button
        commentInput= view.findViewById(R.id.insert_comment) //find edittext
        my_img= view.findViewById(R.id.my_image)
        //init helpers
        c= Constant()
        android_id= getAndroidID(this.context).toString()

        //Edittext break the fullscreen ui. Some adjustment
        commentInput.setOnClickListener({activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)})
        commentInput.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)}

        sendButton.setOnClickListener(this)//set send button for sending comments
        return view

    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Find RV of comments
        val mRecyclerView =  view?.findViewById(R.id.RV_comments) as RecyclerView
        val mLayoutManager = LinearLayoutManager(this.context)
        //mLayoutManager.stackFromEnd = true //in order to visualize always the lates comments
        mRecyclerView.layoutManager = mLayoutManager
    }


    /**
     * On button send click, do postComment() and hide keyboard
     */
    override fun onClick(v: View?) {
        ArticleDetailActivity().hideSystemUI(activity.window.decorView, false)
         if (!commentInput.text.isBlank()) { //If user has written something
             if(CheckNetworking.isNetworkAvailable(this.context)){ //post the comment if there's interne connection
                 val usrId= getUsrID() //get user id by it's android_id + article url
                 my_img.text=usrId //set personal userId near of edittext
                 CreateComment.post(context, commentInput.text.toString(), articleUrl, android_id, usrId) //make a post request
             }
             else Toast.makeText(this.context, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()
             commentInput.text.clear()  //clear edit text input
         }
        //Hide keyboard after send comment
        val editV= this.activity.currentFocus
        if(editV!=null){
            val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(editV.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    /**
     * Get user id by it's android_id + article url
     */
    private fun getUsrID() : String {
        //set background color
        val backgroundColor=getColor(android_id, articleUrl)
        user_image.background.setTint(backgroundColor) //for
        my_img.background.setTint(backgroundColor)


        //get and set user id
        var result= RetrieveUsrID.MakeNetworkRequestAsyncTask().execute(articleUrl, android_id).get()

        return if(result != "") //Usr has already written
                    result
                else{ //Usr has not already written, get last Usr and add 1
                    result=RetrieveUsrID.MakeNetworkRequestAsyncTask().execute(articleUrl, null).get()

                    if(result != ""){ //another usr has already written
                        (result.toInt()+1).toString()
                    }
                    else{//usr comment is first comment
                        "1"
                    }
                }
    }
}