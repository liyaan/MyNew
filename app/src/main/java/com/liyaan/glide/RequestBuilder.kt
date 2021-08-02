package com.liyaan.glide

import android.content.Context
import android.widget.ImageView
import com.liyaan.glide.ext.isOnMainThread
import com.liyaan.glide.ext.printThis
import com.liyaan.glide.load.resource.bitmap.DownsampleStrategy
import com.liyaan.glide.request.BaseRequestOptions
import com.liyaan.glide.request.Request
import com.liyaan.glide.request.RequestListener
import com.liyaan.glide.request.SingleRequest
import com.liyaan.glide.request.target.ViewTarget
import java.util.concurrent.Executor
import com.liyaan.glide.request.target.Target
import com.liyaan.glide.util.Executors

class RequestBuilder<TranscodeType>(
    val kGlide: KGlide,
    val requestManager: RequestManager,
    val transcodeClass: Class<TranscodeType>,
    val context: Context
) : BaseRequestOptions<RequestBuilder<TranscodeType>>(),
    ModelTypes<RequestBuilder<TranscodeType>> {

    private var glideContext: GlideContext = kGlide.getGlideContext()
    var isModelSet = false
    lateinit var model: Any

    override fun load(string: String): RequestBuilder<TranscodeType> {
        model = string
        isModelSet = true
        return this
    }

    fun into(view: ImageView): ViewTarget<ImageView, TranscodeType> {
        require(isOnMainThread()) { "must load on main thread" }
        val requestOptions: BaseRequestOptions<*> = this
        printThis("DownsampleStrategy =${getOptions().get<DownsampleStrategy>(DownsampleStrategy.OPTION)!!.javaClass.simpleName}")
        if (!requestOptions.isTransformationSet() && requestOptions.isTransformationAllowed()
            && view.scaleType != null
        ) {
            when (view.scaleType) {
                ImageView.ScaleType.CENTER_CROP -> {
                }
                ImageView.ScaleType.CENTER_INSIDE -> {
                }
                ImageView.ScaleType.FIT_XY -> {
                }
                ImageView.ScaleType.FIT_CENTER,
                ImageView.ScaleType.FIT_START,
                ImageView.ScaleType.FIT_END
                -> {
                }
            }
        }
        return into(
            glideContext.buildImageViewTarget(view, transcodeClass), null, requestOptions,
            Executors.mainThreadExecutor()
        )
    }

    private fun <Y : Target<TranscodeType>> into(
        target: Y,
        targetListener: RequestListener<TranscodeType>?,
        options: BaseRequestOptions<*>,
        callbackExecutor: Executor
    ): Y {
        require(isModelSet) { "You must call #load() before calling #into()" }
        val request = buildRequest(target, targetListener, options, callbackExecutor);
        if (target.getRequest()!=null){

        }
        requestManager.clear(target)
        target.setRequest(request)
        requestManager.track(target, request)
        return target
    }

    private fun buildRequest(
        target: Target<TranscodeType>,
        targetListener: RequestListener<TranscodeType>?,
        requestOptions: BaseRequestOptions<*>,
        callbackExecutor: Executor
    ): Request {
        return SingleRequest.obtain(
            context,
            glideContext,
            model,
            transcodeClass,
            requestOptions,
            requestOptions.getOverrideHeight(),
            requestOptions.getOverrideHeight(),
            Priority.NORMAL,
            target,
            targetListener,
            null,
            null,
            kGlide.engine,
            null,
            callbackExecutor
        )
    }
}