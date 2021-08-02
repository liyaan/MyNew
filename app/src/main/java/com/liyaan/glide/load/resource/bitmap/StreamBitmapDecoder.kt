package com.liyaan.glide.load.resource.bitmap

import android.graphics.Bitmap
import com.liyaan.glide.ext.printThis
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.ResourceDecoder
import com.liyaan.glide.load.engine.Resource
import com.liyaan.glide.load.engine.bitmap_recycle.ArrayPool
import java.io.InputStream

class StreamBitmapDecoder(
    val downsampler: Downsampler,
    byteArrayPool: ArrayPool
) : ResourceDecoder<InputStream, Bitmap> {

    override fun handles(source: InputStream, options: Options): Boolean {
        return downsampler.handles(source)
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Bitmap>? {
        printThis(" decode -> width=$width , height=$height")
        var callbacks: Downsampler.DecodeCallbacks?=null
        return downsampler.decode(source  ,width,height,options,callbacks)
    }
}