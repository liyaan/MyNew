package com.liyaan.glide.load.engine

import androidx.core.util.Pools
import com.liyaan.glide.ext.printThis

import com.liyaan.glide.load.Options
import com.liyaan.glide.load.data.DataRewinder

class LoadPath<Data, ResourceType, Transcode>(
    dataClass: Class<Data>,
    resourceClass: Class<ResourceType>, transcodeClass: Class<Transcode>,
    val decodePaths: List<DecodePath<Data, ResourceType, Transcode>>,
    listPool: Pools.Pool<List<Throwable>>?=null
) {
    fun load(
        rewinder: DataRewinder<Data>,
        options: Options,
        width: Int,
        height: Int,
        decodeCallback: DecodePath.DecodeCallback<ResourceType>
    ): Resource<Transcode>? {
        printThis("load")
        return loadWithExceptionList(rewinder, options, width, height, decodeCallback)
    }

    private fun loadWithExceptionList(
        rewinder: DataRewinder<Data>,
        options: Options,
        width: Int,
        height: Int,
        decodeCallback: DecodePath.DecodeCallback<ResourceType>
    ): Resource<Transcode>? {
        var result: Resource<Transcode>? = null
        decodePaths.forEach {
            try {
                result = it.decode(rewinder, width, height, options, decodeCallback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (result != null) {
                return result
            }
        }

        return result
    }

}