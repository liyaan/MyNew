package com.liyaan

import android.content.Context
import androidx.multidex.MultiDex.install
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication(){

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        install(this)
    }
}