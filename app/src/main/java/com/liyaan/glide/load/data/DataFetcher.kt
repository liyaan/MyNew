package com.liyaan.glide.load.data

import com.liyaan.glide.Priority
import com.liyaan.glide.load.DataSource

interface DataFetcher<T> {

    interface DataCallback<T>{
        fun onDataReady(data: T)
        fun onLoadFailed(e: Exception)
    }
    fun loadData(priority: Priority, callback:DataCallback<in T>)
    fun cleanup()
    fun cancel()
    fun getDataClass(): Class<T>
    fun getDataSource(): DataSource
}