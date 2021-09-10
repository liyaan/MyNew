package com.liyaan.proxy

import com.liyaan.retrofitlibrary.Field
import com.liyaan.retrofitlibrary.GET
import com.liyaan.retrofitlibrary.POST
import com.liyaan.retrofitlibrary.Query
import okhttp3.Call


interface WeatherApi {
    @POST("")
    fun postWeather(
        @Field("phone") phone: String?
    ): Call?

    @GET("")
    fun getWeather(
        @Query("phone") phone: String?
    ): Call?
}