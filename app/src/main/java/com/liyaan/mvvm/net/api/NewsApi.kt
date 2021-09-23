package com.liyaan.mvvm.net.api

import com.liyaan.mvvm.net.ui.bean.DataBean
import com.liyaan.mvvm.net.ui.bean.Result

import com.liyaan.mvvm.net.ui.bean.ResultBean

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi {

    @GET("index")
    fun getNews(
        @Query("type") type: String?,
        @Query("key") key: String?
    ): Call<Result<ResultBean<DataBean>>>

}