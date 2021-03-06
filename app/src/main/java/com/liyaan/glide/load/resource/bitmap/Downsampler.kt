package com.liyaan.glide.load.resource.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.liyaan.glide.ext.printThis
import com.liyaan.glide.load.DecodeFormat_DEFAULT
import com.liyaan.glide.load.Option
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.PreferredColorSpace
import com.liyaan.glide.load.engine.Resource
import com.liyaan.glide.load.engine.bitmap_recycle.ArrayPool
import com.liyaan.glide.load.engine.bitmap_recycle.BitmapPool
import com.liyaan.glide.load.engine.resource.bitmap.BitmapResource
import com.liyaan.glide.util.Util
import java.io.IOException
import java.io.InputStream

class Downsampler(
    val bitmapPool: BitmapPool, val byteArrayPool: ArrayPool
) {
    companion object{
//        val DECODE_FORMAT = Option.memory(
//            "com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeFormat", DecodeFormat_DEFAULT
//        )
//        val PREFERRED_COLOR_SPACE = Option.memory(
//            "com.bumptech.glide.load.resource.bitmap.Downsampler.PreferredColorSpace", PreferredColorSpace.SRGB
//        )
        val DOWNSAMPLE_STRATEGY =DownsampleStrategy.OPTION;
    }
    fun handles(source: InputStream): Boolean {
        return true
    }

    fun decode(
        ris: InputStream,
        width: Int,
        height: Int,
        options: Options,
        callbacks: DecodeCallbacks?
    ): Resource<Bitmap>? {
        var bitmap: Bitmap
        val options = BitmapFactory.Options()
        ris.reset()
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ris,null,options)
        options.inJustDecodeBounds = false;
        val sourceHeight =options.outHeight
        val sourceWidth =options.outWidth
        printThis("sourceHeight =$sourceHeight sourceWidth =$sourceWidth")
//        options.inSampleSize =8
        options.inTargetDensity=width
        options.inDensity=sourceWidth
        options.inScaled=true
        //把流回到起点
        ris.reset()
        bitmap = BitmapFactory.decodeStream(ris,null,options)!!
        printThis("bitmap size = ${Util.getBitmapByteSize(bitmap)}")
        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    interface DecodeCallbacks {
        fun onObtainBounds()

        @Throws(IOException::class)
        fun onDecodeComplete(bitmapPool: BitmapPool?, downsampled: Bitmap?)
    }
}