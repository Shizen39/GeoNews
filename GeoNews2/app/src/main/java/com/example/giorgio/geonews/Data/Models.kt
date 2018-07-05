package com.example.giorgio.geonews.Data

/**
 * Created by giorgio on 01/07/18.
 * Model;
 * Custom data class that represents an article from a news source.
 */

class News(val articles: List<Article>)
class Article (val source: Source, val author: String, val title: String,
               val description: String, val url: String, val urlToImage: String,
               val publishedAt: String)
class Source(val id:String, val name: String)

class Social(val comments: List<UsrComment>)
class UsrComment(val comment: String, val usrImg: String)