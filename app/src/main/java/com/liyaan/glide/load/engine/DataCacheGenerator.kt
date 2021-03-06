package com.liyaan.glide.load.engine


import com.liyaan.glide.ext.printThis
import com.liyaan.glide.load.DataSource
import com.liyaan.glide.load.Key
import com.liyaan.glide.load.data.DataFetcher
import com.liyaan.glide.load.model.ModelLoader
import java.io.File

//cacheKeys = helper.getCacheKeys()
class DataCacheGenerator(
    val helper: DecodeHelper<*>,
    var cacheKeys: List<Key> = helper.getCacheKeys(),
    val cb: DataFetcherGenerator.FetcherReadyCallback
) : DataFetcherGenerator, DataFetcher.DataCallback<Any?> {

    private var sourceKey: Key? = null
    private var modelLoaders: List<ModelLoader<File, *>>? = null

    @Volatile
    private var loadData: ModelLoader.LoadData<*>? = null
    private var cacheFile: File? = null
    private var modelLoaderIndex = 0
    private var sourceIdIndex = -1

    override fun startNext(): Boolean {
        printThis("startNext() " + Thread.currentThread().name)
        while (modelLoaders == null || !hasNextModelLoader()) {
            sourceIdIndex++
            if (sourceIdIndex >= cacheKeys.size) {
                return false
            }
            val sourceId = cacheKeys.get(sourceIdIndex);
            val originalKey = DataCacheKey(sourceId, helper.signature)
            cacheFile = helper.getDiskCache()[originalKey]
            if (cacheFile != null) {
                this.sourceKey = sourceId
                modelLoaders = helper.getModelLoaders(cacheFile!!)
                modelLoaderIndex = 0
            }
        }

        loadData = null
        var started = false
        while (!started && hasNextModelLoader()) {
            val modelLoader = modelLoaders!!.get(modelLoaderIndex++);
            printThis("modelLoader = ${modelLoader.javaClass}")
            loadData = modelLoader.buildLoadData(
                cacheFile!!, helper.width, helper.height, helper.options
            )
            if (loadData != null && helper.hasLoadPath(loadData!!.fetcher.getDataClass())) {
                started = true
                loadData!!.fetcher.loadData(helper.getPriority(), this)
            }
        }
        return started
    }

    private fun hasNextModelLoader(): Boolean {
        return modelLoaderIndex < modelLoaders!!.size
    }

    override fun cancel() {
    }

    override fun onDataReady(data: Any?) {
        printThis("onDataReady data =${data!!.javaClass}")
        cb.onDataFetcherReady(
            sourceKey!!,
            data,
            loadData!!.fetcher,
            DataSource.DATA_DISK_CACHE,
            sourceKey!!
        )
    }

    override fun onLoadFailed(e: Exception) {
        cb.onDataFetcherFailed(sourceKey!!, e, loadData!!.fetcher, DataSource.DATA_DISK_CACHE)
    }
}