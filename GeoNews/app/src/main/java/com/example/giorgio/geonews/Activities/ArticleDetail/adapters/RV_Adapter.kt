package com.example.giorgio.geonews.Activities.ArticleDetail.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.giorgio.geonews.Data.DB.getColor
import com.example.giorgio.geonews.Data.Social
import com.example.giorgio.geonews.Data.UsrComment
import com.example.giorgio.geonews.R
import kotlinx.android.synthetic.main.row_comments.view.*

/**
 * Created by giorgio on 03/07/18.
 * The adapter creates new items in the form of ViewHolders,
 * populates the ViewHolders with data, and returns information about the data.
 */
class RecyclerViewAdapter(val social: Social): RecyclerView.Adapter<CustomViewHolder>() {

    // Create new views and inflate it
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val comment_row= inflater.inflate(R.layout.row_comments, parent, false)
        return CustomViewHolder(comment_row)
    }

    // Replace the contents of a view, binding the list items to TextView
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val comment= social.comments[position]
        holder.view.user_comment.text= comment.comment
        holder.view.user_image.text= comment.usr

        holder.view.user_image.background.setTint(getColor(comment.android_id, comment.url))


        //bind article url and holder url
        holder.comment= comment

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return if(social.comments!=null) social.comments.count() else 0
    }
}
/**
 * A CustomViewHolder is used to cache the view objects in order to save memory.
 */
class CustomViewHolder(val view: View, var comment: UsrComment?=null): RecyclerView.ViewHolder(view) {

/*
    companion object {
        val ARTICLE_LINK_KEY= "ARTICLE_LINK"
    }*/


}