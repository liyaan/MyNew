package com.liyaan.okhttp.interceptor

import com.liyaan.okhttp.Request
import com.liyaan.okhttp.Response

class RealInterceptorChain(interceptorList:MutableList<Interceptor>,index:Int,request:Request):Interceptor.Chain {
    val interceptorList = interceptorList
    val index = index
    val request = request
    override fun request(): Request? {
        return request
    }

    override fun proceed(request: Request?): Response? {
        val chain = RealInterceptorChain(interceptorList,index+1,request!!)
        val interceptor = interceptorList[index]
        val response = interceptor.intercept(chain)
        return response
    }
}