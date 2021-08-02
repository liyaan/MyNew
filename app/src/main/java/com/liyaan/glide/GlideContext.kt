package com.liyaan.glide

import android.content.Context
import android.content.ContextWrapper
import android.widget.ImageView
import com.liyaan.glide.load.engine.bitmap_recycle.ArrayPool
import com.liyaan.glide.request.target.ImageViewTargetFactory
import com.liyaan.glide.request.target.ViewTarget

class GlideContext(base:Context,val arrayPool: ArrayPool):ContextWrapper(base.applicationContext) {
    private val registry =Registry()
    fun getRegistry()=registry
    private val imageViewTargetFactory: ImageViewTargetFactory = ImageViewTargetFactory()

    fun <X>  buildImageViewTarget(
        view: ImageView,
        transcodeClass: Class<X>
    ): ViewTarget<ImageView, X> {
        return  imageViewTargetFactory.buildTarget(view,transcodeClass)
    }
}