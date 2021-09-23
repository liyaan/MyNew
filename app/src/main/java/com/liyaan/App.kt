package com.liyaan

import android.content.Context
import androidx.multidex.MultiDex.install
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication(){

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        install(this)
    }



    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }
    companion object{
        private var mContext: Context? = null
        fun getContext(): Context {
            return mContext!!
        }
    }

}