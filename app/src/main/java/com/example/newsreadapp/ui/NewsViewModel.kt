package com.example.newsreadapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsreadapp.NewsApplication
import com.example.newsreadapp.models.Article
import com.example.newsreadapp.models.NewsResponse
import com.example.newsreadapp.repository.NewsRepository
import com.example.newsreadapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository //parameter
) : AndroidViewModel(app){:wq
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    //Pagination
    var breakingNewsPage= 1
    var searchNewsPage= 1
    var breakingNewsResponse : NewsResponse? = null
    var searchNewsResponse : NewsResponse? = null


    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode: String)= viewModelScope.launch {

        safeBreakingNewsCall(countryCode)
    }


    fun searchNews(searchQuery: String)= viewModelScope.launch {
        safeSearchNewsCall(searchQuery)


    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse== null){
                    breakingNewsResponse= resultResponse //if first page save the result to the response
                }else{
                    val oldArticles= breakingNewsResponse?.articles //else, add all articles to old
                    val newArticle= resultResponse.articles //add new response to new
                    oldArticles?.addAll(newArticle) //add new articles to old articles
                }
                return  Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse== null){
                    searchNewsResponse= resultResponse
                }else{
                    val oldArticles= searchNewsResponse?.articles
                    val newArticle= resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return  Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    fun saveArticle(article: Article)= viewModelScope.launch {
        newsRepository.upsert(article)
    }


    fun getSavedArticle()= newsRepository.getSavedNews()


    fun deleteSavedArticle(article: Article)= viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try{
            if (hasInternetConnection()){
                val response= newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                //handling response
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else-> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try{
            if (hasInternetConnection()){
                val response= newsRepository.searchNews(searchQuery, searchNewsPage)

                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else-> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun hasInternetConnection(): Boolean{
        val connectivityManager= getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork= connectivityManager.activeNetwork?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else -> false
        }
    }
}