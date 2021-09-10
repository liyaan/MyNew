package com.liyaan.proxylibrary

import java.lang.reflect.Proxy

class LogProxy {

    fun log(porxy:ProxyInterface){
        val p:ProxyInterface = Proxy.newProxyInstance(
            ProxyInterface::class.java.classLoader,
            arrayOf(ProxyInterface::class.java),DynamicProxy(porxy)
        ) as ProxyInterface
        p.proxy()
        p.ProxyMethod("message")
    }
}