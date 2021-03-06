package com.liyaan.glide.request.target

import android.graphics.drawable.Drawable
import com.jansir.kglide.request.transition.Transition

import com.liyaan.glide.manager.LifecycleListener
import com.liyaan.glide.request.Request

interface Target<R> : LifecycleListener {
    companion object{
        var SIZE_ORIGINAL = Int.MIN_VALUE
    }
    fun onLoadStarted(placeholder: Drawable?)
    fun onLoadFailed(errorDrawable: Drawable?)
    //泛型in 相当于 java 中 ? super
    fun onResourceReady(resource: R, transition: Transition<R?>?)
    fun onLoadCleared(placeholder: Drawable?)
    fun getSize(cb: SizeReadyCallback)
    fun removeCallback(cb: SizeReadyCallback)
    fun setRequest(request: Request?)
    /** Retrieves the current request for this target, should not be called outside of Glide.  */
    fun getRequest(): Request?
}


