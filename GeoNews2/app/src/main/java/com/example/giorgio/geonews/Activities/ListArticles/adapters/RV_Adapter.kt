package com.example.giorgio.geonews.Activities.ListArticles.adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.giorgio.geonews.Activities.ArticleDetail.ArticleDetailActivity
import com.example.giorgio.geonews.Data.Article
import com.example.giorgio.geonews.Data.News
import com.example.giorgio.geonews.Data.formatter.formatDate
import com.example.giorgio.geonews.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_articles.view.*

/**
 * Created by giorgio on 01/07/18.
 * The adapter creates new items in the form of ViewHolders,
 * populates the ViewHolders with data, and returns information about the data.
 */
class RecyclerViewAdapter(val news: News): RecyclerView.Adapter<CustomViewHolder>() {

    // Create new views and inflate it
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val articles_row= inflater.inflate(R.layout.row_articles, parent, false)
        return CustomViewHolder(articles_row)
    }

    // Replace the contents of a view, binding the list items to TextView
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val article= news.articles[position]
        holder.view.title.text= article.title
        holder.view.source.text= article.source.name
        holder.view.description.text= article.description

        //Set image
        Picasso.get().load(article.urlToImage).placeholder(R.mipmap.ic_launcher)
                .resize(500,250)
                .centerCrop().into(holder.view.urlToImage)

        //Set publishedAt in form of "x time ago"
        holder.view.publishedAt.text= formatDate(article.publishedAt)

        //bind article url and holder url
        holder.article= article
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return news.articles.count()
    }
}
/**
 * A CustomViewHolder is used to cache the view objects in order to save memory.
 */
class CustomViewHolder(val view: View, var article: Article?=null): RecyclerView.ViewHolder(view) {

    companion object {
        val ARTICLE_LINK_KEY= "ARTICLE_LINK"
    }
    init {
        view.setOnClickListener {
            val intent= Intent(view.context, ArticleDetailActivity::class.java)

            intent.putExtra(ARTICLE_LINK_KEY, article?.url)
            view.context.startActivity(intent)
        }
    }

}