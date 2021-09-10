package com.liyaan.proxylibrary

import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class DynamicProxyHandler(private val obj:Any ): InvocationHandler {
    private val objAny = obj
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
        Log.i("aaaaaa","aaaaaaaaaaaaaaaaaaaaa")
        val invoke: Any? = method?.invoke(objAny,args)
        Log.i("aaaaaa","bbbbbbbbbbbbbbbbbbbbb")
        return invoke
    }
}