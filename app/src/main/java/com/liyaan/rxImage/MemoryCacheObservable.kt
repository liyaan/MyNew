package com.liyaan.rxImage

import android.graphics.Bitmap
import android.util.LruCache

class MemoryCacheObservable: AbstractCacheObservable(){
    private val maxMemory = (Runtime.getRuntime().maxMemory()/1024).toInt()
    private val cacheSize = maxMemory/8
    private val bitmapLruCache = object:LruCache<String, Bitmap>(maxMemory){
        override fun sizeOf(key: String?, value: Bitmap): Int {
            return value.rowBytes*value.height/2014
        }
    }
    override fun putImage(image: Image?) {
        bitmapLruCache.put(image!!.url,image.bitmap)
    }

    override fun getImage(url: String?): Image? {
        val bitmap = bitmapLruCache.get(url)
        val image = Image(url!!,bitmap)
        return image
    }
}