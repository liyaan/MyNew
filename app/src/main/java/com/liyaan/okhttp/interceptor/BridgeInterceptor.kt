package com.liyaan.okhttp.interceptor

import com.liyaan.okhttp.Response

class BridgeInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        request?.header("Connection","keep-alive")
        if (request?.requestBody()!=null){
            val requestBody = request.requestBody()
            //文件的类型
            request.header("Content-Type", requestBody!!.getContentType())
            //要塞给对方多少东西
            request.header(
                "Content-Length",requestBody.getContentLength().toString()
            )
            request.header("Content-Type","application/json; charset=UTF-8")
            request.header("Accept","application/json")
        }
        return chain.proceed(request)
    }
}