package com.liyaan.okhttp

class OkHttpClient(builder:Builder){
    var dispatcher:Dispatcher? = builder.dispatcher
    constructor():this(Builder())

    fun newCall(request:Request):Call?{
        return RealCall.newCall(request,this)
    }

    class Builder() {
        var dispatcher: Dispatcher = Dispatcher()
        fun builder(): OkHttpClient {
            return OkHttpClient(this)
        }
    }
}