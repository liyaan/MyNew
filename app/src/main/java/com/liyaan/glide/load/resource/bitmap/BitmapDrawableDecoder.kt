package com.liyaan.glide.load.resource.bitmap

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.ResourceDecoder
import com.liyaan.glide.load.engine.Resource

class BitmapDrawableDecoder<DataType>(val resources: Resources, val decoder: ResourceDecoder<DataType, Bitmap>) :
    ResourceDecoder<DataType, BitmapDrawable> {
    override fun handles(source: DataType, options: Options): Boolean {
        return decoder.handles(source,options)
    }

    override fun decode(source: DataType, width: Int, height: Int, options: Options): Resource<BitmapDrawable>? {
        val bitmapResource =decoder.decode(source, width, height, options)
        return LazyBitmapDrawableResource.obtain(resources,bitmapResource)
    }

}