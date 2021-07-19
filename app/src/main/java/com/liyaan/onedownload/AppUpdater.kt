package com.liyaan.onedownload

class AppUpdater private constructor(){
    private var mNetManager:INetManager = OkHttpManager()
    companion object{
        var mIntance:AppUpdater? = null
        fun getInstance():AppUpdater = mIntance?: synchronized(this){
            mIntance?:AppUpdater().also {
                mIntance = it
            }
        }
    }

    fun setNetManager(netManager:INetManager){
        this.mNetManager = netManager
    }
    fun getNetManager():INetManager = mNetManager
}