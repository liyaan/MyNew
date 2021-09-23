package com.liyaan.mvvm.net.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpManager {
    const val BASEURL = "http://v.juhe.cn/toutiao/"
    fun getRetrofit():Retrofit{
        return Retrofit.Builder().baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}