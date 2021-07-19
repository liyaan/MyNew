package com.liyaan.okhttp.interceptor

import com.liyaan.okhttp.Request
import com.liyaan.okhttp.Response
import java.io.IOException

interface Interceptor {
    @Throws(IOException::class)
    fun intercept(chain:Chain): Response?
    interface Chain{
        fun request(): Request?

        @Throws(IOException::class)
        fun proceed(request: Request?): Response?
    }
}