package com.example.giorgio.geonews.Networking

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.giorgio.geonews.Activities.ArticleDetail.ArticleCommentFragment
import com.example.giorgio.geonews.Activities.ArticleDetail.adapters.RecyclerViewAdapter
import com.example.giorgio.geonews.Data_utils.DB.Constant
import com.example.giorgio.geonews.Data_utils.Social
import com.example.giorgio.geonews.Data_utils.UsrComment
import com.example.giorgio.geonews.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_comments.*
import okhttp3.*
import java.io.IOException




/**
 * Created by giorgio on 09/07/18.
 * Singleton class that consists of helper methods used for requesting and retrieving comments data from geonews.altervista.org
 * (# clickInterface) this.onResponse().adapter(object) --> ArticleDetail.RV_Adatper -
 *                                  - (which calls -> onBindViewHolder().bind() that create -> (Interface) OnItemLongClickListener -> OnItemClick -> popupMenu )
 * (3) this.onResponse().adapter        -> commentFragment.sendB.onCLick())
 */

object Commenting{
    /**
     * Fetch all comments
     */
    fun fetchComments(context: Context, articleUrl:String){
        val url = Constant().READ_ALL+"?url="+"\""+articleUrl+"\""
        val client = OkHttpClient()
        val req = Request.Builder().url(url).build()

        client.newCall(req).enqueue(object : Callback {                         // cannot use .execute() in the UI thread
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()                           // json body response
                val social:Social
                if (!body.equals("{\"comments\":[]}")) {                   // if list of comments isn't empty
                    val gson = GsonBuilder().create()
                    social = gson.fromJson(body, Social::class.java)             // Bind models and json fields
                }
                else{                                                            // Comment placeholder (nothing to show)
                    val comment= listOf(UsrComment("0","Nothing to show", "http://www.nope.it", "nope", " ", " "))
                    social= Social(comment)
                }

                /* Send obj to the adapter in a background thread */
                (context as Activity).runOnUiThread {
                    context.RV_comments.adapter = RecyclerViewAdapter(social,
                            object : RecyclerViewAdapter.OnItemLongClickListener {                  /** (#) -> Implements (CustomViewHolder).OnItemLongClickListener.onItemCLick() */
                        override fun onItemClick(item: UsrComment, update: Boolean, view: View?) {  /** (3)(4) -> Implements onItemClicks function */
                                                if(update) {                                         // Usr popupMenu is Update
                                val frag= context.fragmentManager.findFragmentById(R.id.F_comments) as (ArticleCommentFragment)
                                frag.commentInput.setText(item.comment)
                                /* request editText focus and show keyboard */
                                frag.commentInput.requestFocus()
                                val imm = frag.view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.showSoftInput(frag.commentInput, InputMethodManager.SHOW_IMPLICIT)
                                /* "updating" true so user can modify  comment in commentFragment */
                                frag.updating=true                                                  /** (3) updateComment() called on -> commentFragment.sendB.onCLick()) */
                                frag.oldItem= item
                            }else{                                                                  // Usr popupMenu is Delete
                                if(CheckNetworking.isNetworkAvailable(context))
                                    DeleteComment.deleteComment(context, item.url, item.id)         /** (4) -> CommentsUtils.deleteComment() */
                                else Toast.makeText(context, "No internet connection. " +
                                        "Please check and try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            }
            override fun onFailure(call: Call?, e: IOException?) {
                println("Nop")
                Toast.makeText(context, "Failed to fetch data. Retry later.", Toast.LENGTH_LONG).show()
            }
        })
    }
}


