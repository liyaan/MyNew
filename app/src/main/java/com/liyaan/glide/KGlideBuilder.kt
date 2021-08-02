package com.liyaan.glide

import android.content.Context
import com.liyaan.glide.load.engine.Engine
import com.liyaan.glide.load.engine.bitmap_recycle.LruArrayPool
import com.liyaan.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.liyaan.glide.load.engine.cache.DiskCache
import com.liyaan.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.liyaan.glide.load.engine.cache.LruResourceCache
import com.liyaan.glide.load.engine.cache.MemoryCache
import com.liyaan.glide.load.engine.exector.GlideExecutor
import com.liyaan.glide.manager.ConnectivityMonitorFactory
import com.liyaan.glide.manager.DefaultConnectivityMonitorFactory
import com.liyaan.glide.manager.RequestManagerRetriever
import com.liyaan.glide.request.RequestOptions

internal class KGlideBuilder {

    private val defaultRequestOptionsFactory: KGlide.RequestOptionsFactory =
        object : KGlide.RequestOptionsFactory {
            override fun build(): RequestOptions {
                return RequestOptions()
            }
        }
    var connectivityMonitorFactory: ConnectivityMonitorFactory? = null
    var animationExecutor: GlideExecutor? = null
    var sourceExecutor: GlideExecutor? = null
    var diskCacheExecutor: GlideExecutor? = null
    var memoryCache: MemoryCache? = null
    var diskCacheFactory: DiskCache.Factory? = null
    var engine: Engine? = null
    val isActiveResourceRetentionAllowed = false
    fun build(context: Context): KGlide {
        if (sourceExecutor == null) {
            sourceExecutor = GlideExecutor.newSourceExecutor()
        }
        if (diskCacheExecutor == null) {
            diskCacheExecutor = GlideExecutor.newDiskCacheExecutor()
        }
        if (animationExecutor == null) {
            animationExecutor = GlideExecutor.newAnimationExecutor()
        }
        if (connectivityMonitorFactory == null) {
            connectivityMonitorFactory = DefaultConnectivityMonitorFactory()
        }
        if (memoryCache == null) {
            memoryCache = LruResourceCache(100)
        }
        if (diskCacheFactory == null) {
            diskCacheFactory = InternalCacheDiskCacheFactory(context)
        }
        if (engine == null) {
            engine = Engine(
                memoryCache!!,
                diskCacheFactory!!,
                diskCacheExecutor!!,
                sourceExecutor!!,
                GlideExecutor.newUnlimitedSourceExecutor(),
                animationExecutor!!,
                isActiveResourceRetentionAllowed = isActiveResourceRetentionAllowed
            )
        }
        val requestManagerRetriever = RequestManagerRetriever()
        val connectivityMonitorFactory = DefaultConnectivityMonitorFactory()
        val bitmapPool = LruBitmapPool()
        val arrayPool = LruArrayPool()
        return KGlide(
            context,
            engine!!,
            memoryCache!!,
            requestManagerRetriever,
            connectivityMonitorFactory,
            bitmapPool,
            arrayPool,
            defaultRequestOptionsFactory
        )
    }


}