package com.liyaan.rxImage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jakewharton.disklrucache.DiskLruCache
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream

class DiskCacheObservable(context:Context): AbstractCacheObservable() {
    private val mContext = context
    private var mDiskLruCache: DiskLruCache? = null

    //缓存20m
    private val maxSize = 20 * 1024 * 1024.toLong()

    init {
        initDiskLruCache()
    }
    override fun putImage(image: Image?) {
        Observable.create(ObservableOnSubscribe<Image> { putDataToDiskCache(image) })
            .subscribeOn(Schedulers.io()).subscribe()
    }

    override fun getImage(url: String?): Image? {
        val bitmap = getDataFromDiskCache(url?:"")
        return Image(url!!,bitmap!!)
    }

    private fun initDiskLruCache(){
        val cacheDir = DiskCacheUtil.getDiskCacheDir(this.mContext,"image_cache")
        if (!cacheDir.exists()){
            cacheDir.mkdirs()
        }
        val versionCode = DiskCacheUtil.getAppVersionCode(mContext)
        mDiskLruCache = DiskLruCache.open(cacheDir,versionCode,1,maxSize)
    }

    private fun putDataToDiskCache(image: Image?){
        val key = DiskCacheUtil.getMd5String(image?.url?:"1")
        val editor = mDiskLruCache?.edit(key)
        editor?.apply {
            val outputStream = this.newOutputStream(0)
            if (saveBitmap(image!!.bitmap,outputStream)){
                editor.commit()
            }else{
                editor.abort()
            }
        }
        mDiskLruCache?.flush()
    }
    private fun saveBitmap(bitmpa:Bitmap,outputStream:OutputStream):Boolean{
        val b = bitmpa.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
        outputStream.flush()
        outputStream.close()
        return b
    }
    private fun getDataFromDiskCache(url:String):Bitmap?{
        var fileDescriptor: FileDescriptor? = null
        var fileInputStream: FileInputStream? = null
        var snapshot: DiskLruCache.Snapshot? = null
        try {
            val key = DiskCacheUtil.getMd5String(url)
            snapshot = mDiskLruCache?.get(key)
            snapshot?.apply {
                fileInputStream = this.getInputStream(0) as FileInputStream
                fileDescriptor = fileInputStream?.fd
            }
            val bitmap:Bitmap by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
                BitmapFactory.decodeFileDescriptor(fileDescriptor)
            }
            return bitmap
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream?.close()
                } catch (e: IOException) {
                }
            }
        }
        return null
    }
}