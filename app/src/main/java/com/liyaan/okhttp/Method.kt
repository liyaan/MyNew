package com.liyaan.okhttp

enum class Method private constructor (s: String) {

    POST("POST"),
    POSTJSON("POST"),
    GET("GET"),

    HEAD("HEAD"),

    DELETE("DELETE"),

    PUT("PUT"),

    PATCH("PATCH");

    var methodName:String = s
    open fun doOutput(): Boolean {
        when (this) {
            POST, PUT -> return true
        }
        return false
    }
}