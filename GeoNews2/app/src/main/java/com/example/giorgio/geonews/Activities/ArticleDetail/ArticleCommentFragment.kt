package com.example.giorgio.geonews.Activities.ArticleDetail

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.giorgio.geonews.Activities.ArticleDetail.adapters.RecyclerViewAdapter
import com.example.giorgio.geonews.Data.Social
import com.example.giorgio.geonews.Data.UsrComment
import com.example.giorgio.geonews.R


/**
 * Created by giorgio on 03/07/18.
 */
class ArticleCommentFragment : Fragment(){

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)

        // Replace 'android.R.id.list' with the 'id' of your RecyclerView

        return view




    }




    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mRecyclerView =  view?.findViewById(R.id.RV_comments) as RecyclerView
        val mLayoutManager = LinearLayoutManager(this.activity);
        Log.d("debugMode", "The application stopped after this");
        mRecyclerView.layoutManager = mLayoutManager;

        var comments: Social
        var comm: List<UsrComment> = arrayListOf(UsrComment("comment1", "usr1"), UsrComment("comment2", "usr2"), UsrComment("comment3", "usr3"))




        comments= Social(comm)

        val mAdapter = RecyclerViewAdapter(comments)
        mRecyclerView.adapter = mAdapter
            }

}