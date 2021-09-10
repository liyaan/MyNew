package com.liyaan.proxy

import android.util.Log
import com.liyaan.proxylibrary.ProxyInterface

class ProxyClass:ProxyInterface {
    override fun proxy() {
        Log.i("aaaaaa","ccccccccccccccccccccc")
    }

    override fun ProxyMethod(message:String) {
        Log.i("aaaaaa","ddddddddddddd$message")
    }
}