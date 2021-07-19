package com.liyaan.okhttp

interface Call {

    fun enqueue(callback:Callback)

    fun execute():Response?
}