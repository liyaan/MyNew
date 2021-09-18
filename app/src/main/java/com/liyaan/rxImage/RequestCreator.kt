package com.liyaan.rxImage

import android.content.Context
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate


class RequestCreator(context:Context) {
    private var memoryCacheObservable: MemoryCacheObservable? = null
    private var diskCacheObservable: DiskCacheObservable? = null
    private var networkCacheObservable: NetworkCacheObservable? = null

    init {
        memoryCacheObservable = MemoryCacheObservable()
        diskCacheObservable = DiskCacheObservable(context)
        networkCacheObservable = NetworkCacheObservable()
    }

    fun getImageFromMemory(url:String): Observable<Image>{
        return memoryCacheObservable!!.requestImage(url)
    }
    fun getImageFromDisk(url:String):Observable<Image>{
        return diskCacheObservable!!.requestImage(url).filter(object: Predicate<Image> {
            override fun test(t: Image): Boolean {
                return t.bitmap!=null
            }

        }).doOnNext { t -> memoryCacheObservable!!.putImage(t) }
    }
    fun getImageFromNetwork(url:String):Observable<Image>{
        return networkCacheObservable!!.requestImage(url).filter(
            object:Predicate<Image>{
                override fun test(t: Image): Boolean {
                    return t.bitmap !=null
                }

            }
        ).doOnNext { t ->
            diskCacheObservable!!.putImage(t)
            memoryCacheObservable!!.putImage(t)
        }
    }

}