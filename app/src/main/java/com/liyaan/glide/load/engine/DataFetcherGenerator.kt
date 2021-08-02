package com.liyaan.glide.load.engine

import com.liyaan.glide.load.DataSource
import com.liyaan.glide.load.Key
import com.liyaan.glide.load.data.DataFetcher

interface DataFetcherGenerator {
    fun startNext():Boolean
    fun cancel()
    interface FetcherReadyCallback{
        fun reschedule()
        fun onDataFetcherReady(
            sourceKey: Key,
            data: Any,
            fetcher: DataFetcher<*>,
            dataSource: DataSource?,
            attemptedKey: Key
        )
        fun onDataFetcherFailed(
            attemptedKey: Key,
            e: Exception?,
            fetcher: DataFetcher<*>,
            dataSource: DataSource
        )
    }
}