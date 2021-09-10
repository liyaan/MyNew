package com.liyaan.retrofitlibrary

import android.util.Log
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Request
import java.lang.reflect.Method

class ServiceMethod {
    private var callFactory: Call.Factory? = null
    private var baseUrl: HttpUrl? = null
    private var httpMethod: String? = null
    private var relativeUrl: String? = null
    private var formBuild: FormBody.Builder? = null
    private var urlBuilder: HttpUrl.Builder? = null

    private val parameterHandlers: Array<ParameterHandler?>

    constructor(builder:Builder){
        callFactory = builder.zyangRetrofit.callFactory
        baseUrl = builder.zyangRetrofit.baseUrl

        httpMethod = builder.httpMethod
        relativeUrl = builder.relativeUrl

        parameterHandlers = builder.parameterHandlers!!

        if (builder.hashBody) {
            formBuild = FormBody.Builder()
        } // 到这里构建已经完成差不多了
    }
    fun invoke(args:Array<Any>):Any{
/*
        1,处理请求的地址与参数
         */
        for (i in parameterHandlers.indices) {
            val handlers = parameterHandlers[i]
            handlers!!.apply(this, args[i].toString())
        }
        val url: HttpUrl
        //获取最终请求地址
        //获取最终请求地址
        if (urlBuilder == null) {
            urlBuilder = baseUrl!!.newBuilder(relativeUrl)
        }
        url = urlBuilder!!.build()

        var formBody: FormBody? = null
        if (formBuild != null) {
            formBody = formBuild!!.build()
        }
        Log.i("aaaa",url.toString())
        val request: Request = Request.Builder().url(url).method(httpMethod, formBody).build()

        return callFactory!!.newCall(request)
    }
    fun addQueryParameterHandler(key: String?, value: String?) {
        if (urlBuilder == null) {
            urlBuilder = baseUrl!!.newBuilder(relativeUrl)
        }
        urlBuilder!!.addQueryParameter(key, value)
    }

    fun addFiledParameter(key: String?, value: String?) {
        formBuild!!.add(key, value)
    }
    class Builder{
        var zyangRetrofit: ZyangRetrofit
        var methodAnnotations: Array<Annotation>
        var parameterAnnotations: Array<Array<Annotation>>
        var httpMethod: String? = null
        var relativeUrl: String? = null
        var hashBody = false
        var parameterHandlers: Array<ParameterHandler?>? = null
        constructor(zyangRetrofit: ZyangRetrofit, method: Method){
            this.zyangRetrofit = zyangRetrofit
            //获取方法上所有的注解
            methodAnnotations = method.annotations
            // 获取方法参数的所有的注解(一个参数可以有多个注解, 一个方法又会有多个参数)
            parameterAnnotations = method.parameterAnnotations
        }
        
        fun build():ServiceMethod{
            methodAnnotations.forEachIndexed { index, methodAnnotation ->
                if (methodAnnotation is POST){
                    this.httpMethod = "POST"
                    this.relativeUrl = (methodAnnotation as POST).value
                    this.hashBody = true
                }else if (methodAnnotation is GET){
                    this.httpMethod = "GET"
                    this.relativeUrl = (methodAnnotation as GET).value
                    this.hashBody = false
                }
            }
            val length = parameterAnnotations.size
            parameterHandlers = arrayOfNulls(length)
            for (i in 0 until length){
                val annotations = parameterAnnotations[i]
                annotations.forEachIndexed { index, annotation ->
                    if (annotation is Field){
                        val value = (annotation as Field).value
                        parameterHandlers!![i] = ParameterHandler.FiledParameterHandler(value)
                    }else if (annotation is Query){
                        val value = (annotation as Query).value
                        parameterHandlers!![i] = ParameterHandler.QueryParameterHandler(value)
                    }
                }
            }
            return ServiceMethod(this)

        }
    }
}