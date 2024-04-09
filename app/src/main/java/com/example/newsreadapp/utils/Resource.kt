package com.example.newsreadapp.utils

sealed class Resource <T>(
    val data: T?=null,
    val message: String?= null,

    ){

    //classes allowed to inherit
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T?= null): Resource<T>(data, message)
    class Loading<T>: Resource<T>()


}