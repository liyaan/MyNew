package com.liyaan.glide.load.engine.resource.bitmap

import android.graphics.Bitmap
import com.liyaan.glide.load.engine.Resource
import com.liyaan.glide.load.engine.bitmap_recycle.BitmapPool
import com.liyaan.glide.util.Util


class BitmapResource(val bitmap:Bitmap ,val bitmapPool: BitmapPool): Resource<Bitmap> {

    companion object{
        fun obtain(bitmap:Bitmap , bitmapPool:BitmapPool):BitmapResource{
          return  BitmapResource(bitmap, bitmapPool)
        }
    }
    override fun getResourceClass(): Class<Bitmap> {
        return Bitmap::class.java
    }

    override fun get(): Bitmap {
        return bitmap
    }

    override fun getSize(): Int {
        return Util.getBitmapByteSize(bitmap)
    }

    override fun recycle() {
        bitmapPool.put(bitmap)
    }
}