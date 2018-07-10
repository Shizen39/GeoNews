package com.example.giorgio.geonews.Activities.ArticleDetail

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.example.giorgio.geonews.Data.DB.Constant
import com.example.giorgio.geonews.Networking.Commenting
import com.example.giorgio.geonews.R
import okhttp3.*
import java.io.IOException




/**
 * Created by giorgio on 03/07/18.
 */
class ArticleCommentFragment : Fragment(), View.OnClickListener {

    /**
     * Init variables
     */

    lateinit var commentInput: EditText     //Edit text for input comment
    lateinit var c: Constant
    lateinit var android_id: String

    lateinit var articleUrl: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_comments, container, false)

        //init send button
        val sendButton: Button = view.findViewById(R.id.send_comment)

        //find edittext
        commentInput= view.findViewById(R.id.insert_comment)

        //init stuffs
        c= Constant()
        android_id= c.getAndroidID(this.context).toString()

        //set send button for sending comments
        sendButton.setOnClickListener(this)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Find RV of comments
        val mRecyclerView =  view?.findViewById(R.id.RV_comments) as RecyclerView
        val mLayoutManager = LinearLayoutManager(this.context)
        mLayoutManager.stackFromEnd = true //in order to visualize always the lates comments
        mRecyclerView.layoutManager = mLayoutManager

        //onB_comment click-> fetchComments (in activity) ->adapter=adapter
    }


    /**
     * On button send click, do postComment() and hide keyboard
     */
    override fun onClick(v: View?) {
         if (!commentInput.text.isBlank()) {
             postComment()
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
     * POST a comment
    */
    fun postComment(){
        val usrId= getUsrID()
        val formBody= FormBody.Builder()
                .add("comment", commentInput.text.toString())
                .add("url", articleUrl)
                .add("android_id", android_id)
                .add("usr", usrId)
                .build()
        val client= OkHttpClient()
        val request= Request.Builder()
                .url(c.INSERT)
                .post(formBody)
                .build()

        client.newCall(request).enqueue(object : Callback { //can't .execute() on the main thread!
            override fun onFailure(call: Call?, e: IOException?) {
                println("Failed to execute request (okhttp)")
            }
            /**
             * GSON: parse json body response's fields, binding them whit Models
             */
            override fun onResponse(call: Call?, response: Response?) {
                //READALLCOMMENTS
                Commenting.fetchComments(context, articleUrl)

            }
        })


    }

    /**
     * Get user id (of actual article comment section) by android_id
     */
    fun getUsrID(): String {
        return 1.toString()
    }





}