package com.liyaan.okhttp

import android.util.Log
import com.liyaan.okhttp.interceptor.*
import java.io.IOException

class RealCall(client:OkHttpClient,originalRequest:Request):Call {
    val client = client
    val originalRequest = originalRequest

    companion object{
        @JvmStatic
        fun newCall(request: Request?, okHttpClient: OkHttpClient?): Call? {
            return RealCall(okHttpClient!!,request!!)
        }
    }
    override fun enqueue(callback: Callback) {

        //异步交给线程池
        val asyncCall: RealCall.AsyncCall = AsyncCall(callback)
        Log.i("aaaa","aaaaaaaaaaaaaaaa")
        client.dispatcher?.enqueue(asyncCall)
    }

    override fun execute(): Response? {
        return null
    }
    inner class AsyncCall(callback:Callback):NamedRunnable(){
        val callback = callback
        override fun execute() {
            val request = originalRequest
            try {
                val interceptors = ArrayList<Interceptor>()
                interceptors.add(RetryAndFollowUpInterceptor())
                interceptors.add(BridgeInterceptor())
                interceptors.add(CacheInterceptor())
                interceptors.add(ConnectInterceptor())
                interceptors.add(CallServerInterceptor())

                val chain = RealInterceptorChain(interceptors,0,originalRequest)
                val response = chain.proceed(request)
                callback.onResponse(this@RealCall,response!!)
            }catch (e:IOException){
                callback.onFailure(this@RealCall,e)
            }
        }

    }
}