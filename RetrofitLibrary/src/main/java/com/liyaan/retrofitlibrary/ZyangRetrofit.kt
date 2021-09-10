package com.liyaan.retrofitlibrary

import android.util.Log
import com.liyaan.retrofitlibrary.ServiceMethod.Builder
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

class ZyangRetrofit {
    val callFactory:Call.Factory
    val baseUrl:HttpUrl
    val serviceMethodMap = ConcurrentHashMap<Method,ServiceMethod>()
    protected constructor(callFactory:Call.Factory,baseUrl:HttpUrl){
        this.callFactory = callFactory
        this.baseUrl = baseUrl
    }

    //通过动态代理构建service接口的代理对象.
    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service)
        ) { proxy, method, args ->
            val serviceMethod = loadServiceMethod(method)
            serviceMethod?.invoke(args)
        } as T
    }

    /*
获取Method解析后的信息
 */
    private fun loadServiceMethod(method: Method): ServiceMethod? {
        var resultService = serviceMethodMap[method]
        if (resultService != null) {
            return resultService
        }
        synchronized(serviceMethodMap) {
            resultService = serviceMethodMap[method]
            if (resultService == null) {
                resultService =
                    ServiceMethod.Builder(this, method).build()
                serviceMethodMap[method] = resultService!!
            }
        }
        return resultService
    }

    class Builder{
        private var baseUrl: HttpUrl? = null

        //okhttp3.Call.Factory 有唯一一个实现类, 是OkhttpClient
        private var callFactory: Call.Factory? = null

        fun callFactory(factory: Call.Factory?):Builder? {
            this.callFactory = factory
            return this
        }

        fun baseUrl(baseUrl: String?): Builder? {
            this.baseUrl = HttpUrl.get(baseUrl)
            Log.i("aaaa",baseUrl!!)
            return this
        }

        fun build(): ZyangRetrofit? {
            checkNotNull(baseUrl) { "Base URL required" }
            var callFactory = this.callFactory
            if (callFactory == null) {
                callFactory = OkHttpClient()
            }
            return ZyangRetrofit(callFactory!!, baseUrl!!)
        }
    }
}