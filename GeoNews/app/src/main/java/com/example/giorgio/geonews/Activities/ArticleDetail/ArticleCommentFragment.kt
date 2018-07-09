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
import com.example.giorgio.geonews.Activities.ArticleDetail.adapters.RecyclerViewAdapter
import com.example.giorgio.geonews.Data.Social
import com.example.giorgio.geonews.Data.UsrComment
import com.example.giorgio.geonews.R


/**
 * Created by giorgio on 03/07/18.
 */
class ArticleCommentFragment : Fragment(), View.OnClickListener {

    //override lifecycle callback to run fragment
   /* override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val inflate= inflater.inflate(R.layout.fragment_comments, container, false)

        val RV= view.findViewById(R.id.RV_comments) as RecyclerView

        var comments: Social
        var comm: ArrayList<UsrComment> = arrayListOf()

        for (i in 0..5){
             comm[i] = UsrComment("comment"+i.toString(), "usr"+i.toString())
        }
        comments= Social(comm)



        RV_comments.layoutManager= LinearLayoutManager(context)
        RV_comments.adapter= RecyclerViewAdapter(comments)

        // Inflate the layout for this fragment
        return inflate
    }*/
    lateinit var commentInput: EditText     //Edit text for input comment
    lateinit var comm: ArrayList<UsrComment>    //list of comments

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_comments, container, false)
        //send button
        val sendButton: Button = view.findViewById(R.id.send_comment)
        //find edittext
        commentInput= view.findViewById(R.id.insert_comment)
        //set send button for sending comments
        sendButton.setOnClickListener(this)
        //init comments list
        comm = arrayListOf()
        return view
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Find RV of comments
        val mRecyclerView =  view?.findViewById(R.id.RV_comments) as RecyclerView
        val mLayoutManager = LinearLayoutManager(this.activity)
        mRecyclerView.layoutManager = mLayoutManager


        var comments= Social(comm) //all comments (in Social - Model) TODO: FUNZIONE SELECT *

        val mAdapter = RecyclerViewAdapter(comments)
        mRecyclerView.adapter = mAdapter
    }




    override fun onClick(v: View?) {
         if (!commentInput.text.isBlank()) {
             comm.add(UsrComment(commentInput.text.toString(), "usr1")) //add comment TODO:FUNZIONE INSERT INTO
             commentInput.text.clear()  //clear edi text input
         }
        //Hide keyboard after send comment
        val editV= this.activity.currentFocus
        if(editV!=null){
            val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(editV.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }



}