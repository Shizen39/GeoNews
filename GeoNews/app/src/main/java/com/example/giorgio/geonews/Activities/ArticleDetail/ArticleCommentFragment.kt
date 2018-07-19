package com.example.giorgio.geonews.Activities.ArticleDetail

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
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
import com.example.giorgio.geonews.Data_utils.DB.getAndroidID
import com.example.giorgio.geonews.Data_utils.DB.getColor
import com.example.giorgio.geonews.Data_utils.UsrComment
import com.example.giorgio.geonews.Networking.*
import com.example.giorgio.geonews.R
import kotlinx.android.synthetic.main.row_comments.*


/**
 * Created by giorgio on 03/07/18.
 * Fragment attached to ArticleDetailActivity that show latest user comments in a RV, calling fetchComments and using CommentsUtils and UserUtils
 * -> Networking.fetchComments()
 * -> CommentsUtils.CreateComment() / .UpdateComment() / .DeleteComment() / .RetrieveUsrID()
 * (*) this -> fetchComments.onResponse() -> RV_Adapter.RecyclerViewAdapter
 * GeoNews
 */

class ArticleCommentFragment : Fragment(), View.OnClickListener {

    /** Init variables */
    lateinit var commentInput: EditText     // Edit text for input comment
    lateinit var my_img: TextView           // ImgView for user image id near commentInput
    lateinit var android_id: String         // User android HW id
    lateinit var articleUrl: String         // url of selected article
    var updating= false                     // boolean that check if a user is typing in commentInput because it's updating or creating a new comment
    lateinit var oldItem: UsrComment        // old comment for query purpose, in case user has updated the comment

    /** OnCreateView func */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)   // inflate fragment
        val sendButton: Button = view.findViewById(R.id.send_comment)                           // init send comment button
        commentInput= view.findViewById(R.id.insert_comment)                                    // find editText
        my_img= view.findViewById(R.id.my_image)                                                // find usr image
        android_id= getAndroidID(this.context).toString()                                       // get android HW id (-> UsrUtils)

        /* Edittext break the fullscreen ui. Some adjustment */
        commentInput.setOnClickListener({activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)})
        commentInput.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)}

        sendButton.setOnClickListener(this)                                                     // set send comment button listener for sending comments
        return view
    }

    /** Called after onCreateView */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* Find comments RV */
        val mRecyclerView =  view?.findViewById(R.id.RV_comments) as RecyclerView
        val mLayoutManager = LinearLayoutManager(this.context)
        mRecyclerView.layoutManager = mLayoutManager

        /* Sets up a SwipeRefreshLayout.OnRefreshListener invoked when the user performs a swipe-to-refresh gesture. */
        val mSwipeRefreshLayout = view.findViewById(R.id.swiperefreshComment) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener({
            Commenting.fetchComments(this.context, articleUrl)                                  // (*) this -> fetchComments.onResponse() -> RV_Adapter.RecyclerViewAdapter
            mSwipeRefreshLayout.isRefreshing = false
        })
    }

    /**
     * -> onCreateView.sendButton.
     * On button send comment click, do postComment() and hide keyboard
     */
    override fun onClick(v: View?) {
        if(!updating) {                                                                     // User start typing a new comment (not already existing)
            if (!commentInput.text.isBlank()) {                                             // If user has written something in editText, crate comment (else do nothing)
                if (CheckNetworking.isNetworkAvailable(this.context)) {
                    val usrId = getUsrID()                                                  // get comments user id by fetching on DB ->
                    my_img.text = usrId
                    CreateComment.createComment(context, commentInput.text.toString(),
                            articleUrl, android_id, usrId) //make a createComment request
                } else Toast.makeText(this.context, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()
            }
        }
        else {                                                                              // User is updating his comment
            println(commentInput.text.toString())
            if (commentInput.text.toString() != oldItem.comment) {                          // If user has updated the comment in editText, crate comment (else do nothing)
                if (CheckNetworking.isNetworkAvailable(this.context))
                    UpdateComment.updateComment(this.context, commentInput.text.toString(),
                            oldItem.id, articleUrl)
                else Toast.makeText(this.context, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()
            }
            updating=false                                                                  // updated. Change state
        }
        commentInput.text.clear()                                                           // clear edit text input
        /* Hide keyboard after send comment */
        val editV= this.activity.currentFocus
        if(editV!=null){
            val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(editV.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    /**
     * --> onClick.usrId =
     * Get comments user id by it's android_id + article url
     */
    private fun getUsrID() : String {
        /* set user image  background color */
        val backgroundColor=getColor(android_id, articleUrl)
        user_image.background.setTint(backgroundColor) //for
        my_img.background.setTint(backgroundColor)

        /* get and set comments user id */
        var result= RetrieveUsrID.MakeNetworkRequestAsyncTask().execute(articleUrl, android_id).get()

        return if(result != "")                         // Usr has already written another comment
                    result
                else{                                   // Usr has not already written
                    result=RetrieveUsrID.MakeNetworkRequestAsyncTask().execute(articleUrl, null).get()

                    if(result != ""){                   // Another usr has already written -> get last Usr id + 1
                        (result.toInt()+1).toString()
                    }
                    else{                               // usr comment is first comment -> get 1
                        "1"
                    }
                }
    }
}