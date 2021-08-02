package com.liyaan.glide.load.resource.transcode

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.liyaan.glide.ext.printThis

import com.liyaan.glide.load.Options
import com.liyaan.glide.load.engine.Resource
import com.liyaan.glide.load.engine.resource.transcode.ResourceTranscoder
import com.liyaan.glide.load.resource.bitmap.LazyBitmapDrawableResource

class BitmapDrawableTranscoder (val resources: Resources) :
    ResourceTranscoder<Bitmap, BitmapDrawable> {
    override fun transcode(
        toTranscode: Resource<Bitmap>?,
        options: Options
    ): Resource<BitmapDrawable> {
        printThis("transcode")
        return LazyBitmapDrawableResource.obtain(resources, toTranscode)!!
    }
}