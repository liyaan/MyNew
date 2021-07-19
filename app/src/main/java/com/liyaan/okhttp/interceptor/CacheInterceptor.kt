package com.liyaan.okhttp.interceptor

import com.liyaan.okhttp.Response

class CacheInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        return chain.proceed(request)
    }
}