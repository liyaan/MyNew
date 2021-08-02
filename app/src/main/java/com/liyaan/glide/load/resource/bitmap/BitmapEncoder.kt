package com.liyaan.glide.load.resource.bitmap

import android.graphics.Bitmap
import com.liyaan.glide.load.EncodeStrategy
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.ResourceEncoder
import com.liyaan.glide.load.engine.Resource
import com.liyaan.glide.load.engine.bitmap_recycle.ArrayPool
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class BitmapEncoder(val arrayPool: ArrayPool) : ResourceEncoder<Bitmap> {
    override fun getEncodeStrategy(options: Options): EncodeStrategy {
        //变换之后的
        return EncodeStrategy.TRANSFORMED
    }

    //todo encode还须优化
    override fun encode(resource: Resource<Bitmap>, file: File, options: Options): Boolean {
        val bitmap: Bitmap = resource.get()
        val format: Bitmap.CompressFormat = getFormat(bitmap, options)
        return try {
//            options.get(BitmapEncoder.COMPRESSION_QUALITY)
            val quality: Int = 30
            var success = false
            var os: OutputStream? = null
            try {
                os = FileOutputStream(file)
//                os = BufferedOutputStream(os, arrayPool)
                bitmap.compress(format, quality, os)
                os.close()
                success = true
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (os != null) {
                    try {
                        os.close()
                    } catch (e: IOException) {
                        // Do nothing.
                    }
                }
            }
            success
        } finally {

        }
    }

    private fun getFormat(bitmap: Bitmap, options: Options): Bitmap.CompressFormat {
//        val format: CompressFormat = options.get(BitmapEncoder.COMPRESSION_FORMAT)
//        return format
//            ?: if (bitmap.hasAlpha()) {
//                CompressFormat.PNG
//            } else {
//                CompressFormat.JPEG
//            }
        return  Bitmap.CompressFormat.JPEG
    }
}