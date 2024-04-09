package com.example.newsreadapp.repository

import com.example.newsreadapp.api.RetrofitInstance
import com.example.newsreadapp.db.ArticleDatabase
import com.example.newsreadapp.models.Article


class NewsRepository(
    val db: ArticleDatabase //parameter
) {


    suspend fun getBreakingNews(countryCode:String, pageNumber:Int)=
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)


    suspend fun searchNews(searchQuery: String, pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)


    suspend fun upsert(article: Article)=
        db.getArticleDao().upsert(article)


    fun getSavedNews()=
        db.getArticleDao().getAllArticles()


    suspend fun deleteArticle(article: Article)=
        db.getArticleDao().deleteArticle(article)
}