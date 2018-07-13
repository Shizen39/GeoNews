package com.example.giorgio.geonews.Activities.ArticleDetail.adapters

import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.giorgio.geonews.Data_utils.DB.getColor
import com.example.giorgio.geonews.Data_utils.Social
import com.example.giorgio.geonews.Data_utils.UsrComment
import com.example.giorgio.geonews.Data_utils.formatter.formatDate
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

        holder.view.date.text=formatDate(comment.date)

        holder.view.user_image.background.setTint(getColor(comment.android_id, comment.url)) //Get usr_color by comment url and usr_android_id


        //bind article url and holder url
        holder.comment= comment

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return social.comments.count()
    }
}
/**
 * A CustomViewHolder is used to cache the view objects in order to save memory.
 */
class CustomViewHolder(val view: View, var comment: UsrComment?=null): RecyclerView.ViewHolder(view) {

    init {

        view.setOnLongClickListener{
            println("long clicked pos: ${comment!!.comment}")

            val popupMenu= PopupMenu(view.context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.update -> {
                        Toast.makeText(view.context,"update",Toast.LENGTH_LONG).show()

                        
                        true
                    }
                    R.id.delete -> {
                        Toast.makeText(view.context,"delete",Toast.LENGTH_LONG).show()
                        true
                    }
                    else -> {
                        false
                    }

                }
            }
            popupMenu.inflate(R.menu.menu_main)

            //In order to show Icons. :l
            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
            } catch (e: Exception){
                Toast.makeText(view.context,"Error showing menu icons.",Toast.LENGTH_LONG).show()
                Log.getStackTraceString(e)
            } finally {
                //Show Menu
                popupMenu.show()
            }

            true
        }

    }



}





