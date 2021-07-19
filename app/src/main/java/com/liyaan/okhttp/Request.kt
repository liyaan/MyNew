package com.liyaan.okhttp

import org.json.JSONObject

class Request private constructor(builder: Builder){
    var url:String? = null
    var method:Method? = null
    var headers:HashMap<String,String>? = null
    var requestBody:RequestBody? = null
    var jsonString:String? = null
    init {
        this.url = builder.url
        this.method = builder.method
        this.headers = builder.headers
        this.requestBody = builder.requestBody
        this.jsonString = builder.jsonString
    }

    fun url(): String? {
        return url
    }

    fun method(): Method? {
        return method
    }

    fun header(key: String?, value: String?) {
        headers!![key!!] = value!!
    }

    fun requestBody(): RequestBody? {
        return requestBody
    }
    class Builder{
        var url:String? = null
        var method:Method? = null
        var headers:HashMap<String,String>? = null
        var requestBody:RequestBody? = null
        var jsonString:String? = null
        init {
            method = Method.GET
            headers = HashMap()
        }
        fun url(url:String):Builder{
            this.url = url
            return this
        }
        fun get():Builder{
            method = Method.GET
            return this
        }
        fun post(body:RequestBody):Builder{
            method = Method.POST
            this.requestBody = body
            return this
        }
        fun headers(key:String,value:String):Builder{
            headers?.put(key,value)
            return this
        }
        fun headers(map:HashMap<String,String>?):Builder{
            headers?.putAll(map!!)
            return this
        }
        fun jsonString(jsStr:String):Builder{
            method = Method.POSTJSON
            jsonString = jsStr
            return this
        }
        fun jsonString(jsStr:JSONObject):Builder{
            method = Method.POSTJSON
            jsonString = jsStr.toString()
            return this
        }
        fun builder():Request{
            return Request(this)
        }
    }
}