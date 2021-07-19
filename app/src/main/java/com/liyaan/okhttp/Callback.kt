package com.liyaan.okhttp

import java.io.IOException

interface Callback {

    fun onFailure(call:Call,e:IOException)
    @Throws(IOException::class)
    fun onResponse(call: Call,response: Response)
}