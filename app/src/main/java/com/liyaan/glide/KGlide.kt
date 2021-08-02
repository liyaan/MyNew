package com.liyaan.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.liyaan.glide.load.ResourceDecoder
import com.liyaan.glide.load.data.InputStreamRewinder
import com.liyaan.glide.load.engine.Engine
import com.liyaan.glide.load.engine.bitmap_recycle.ArrayPool
import com.liyaan.glide.load.engine.bitmap_recycle.BitmapPool
import com.liyaan.glide.load.engine.cache.MemoryCache
import com.liyaan.glide.load.model.FileLoader
import com.liyaan.glide.load.model.KGlideUrl
import com.liyaan.glide.load.model.StreamEncoder
import com.liyaan.glide.load.model.StringLoader
import com.liyaan.glide.load.model.stream.HttpGlideUrlLoader
import com.liyaan.glide.load.model.stream.HttpUriLoader
import com.liyaan.glide.load.resource.bitmap.BitmapDrawableDecoder
import com.liyaan.glide.load.resource.bitmap.BitmapEncoder
import com.liyaan.glide.load.resource.bitmap.Downsampler
import com.liyaan.glide.load.resource.bitmap.StreamBitmapDecoder
import com.liyaan.glide.load.resource.transcode.BitmapDrawableTranscoder
import com.liyaan.glide.manager.ConnectivityMonitorFactory
import com.liyaan.glide.manager.RequestManagerRetriever
import com.liyaan.glide.request.RequestOptions
import java.io.File
import java.io.InputStream

class KGlide(
    context: Context,
    val engine: Engine,
    val memoryCache: MemoryCache,
    val requestManagerRetriever: RequestManagerRetriever,
    val connectivityMonitorFactory: ConnectivityMonitorFactory,
    val bitmapPool: BitmapPool,
    val arrayPool: ArrayPool,
    val requestOptionsFactory: RequestOptionsFactory
) {
    private var glideContext = GlideContext(context,arrayPool)

    init {
        val resources =context.resources
        glideContext.getRegistry().apply {
            //model
            append(String::class.java, InputStream::class.java, StringLoader.StreamFactory())
            append(Uri::class.java, InputStream::class.java, HttpUriLoader.Factory())
            append(KGlideUrl::class.java, InputStream::class.java, HttpGlideUrlLoader.Factory())
            append(File::class.java, InputStream::class.java,  FileLoader.StreamFactory())

            //decode
            val streamBitmapDecoder: ResourceDecoder<InputStream, Bitmap>
            streamBitmapDecoder = StreamBitmapDecoder(
                downsampler = Downsampler(bitmapPool, byteArrayPool = arrayPool),
                byteArrayPool = arrayPool
            )
            append(
                Registry.BUCKET_BITMAP,
                InputStream::class.java,
                Bitmap::class.java,
                streamBitmapDecoder
            )
            append(
                Registry.BUCKET_BITMAP_DRAWABLE,
                InputStream::class.java,
                BitmapDrawable::class.java,
                BitmapDrawableDecoder(resources, streamBitmapDecoder)
            )

            //transcode
            register(Bitmap::class.java,BitmapDrawable::class.java,
                BitmapDrawableTranscoder(resources)
            )
            register(InputStreamRewinder.Factory(arrayPool))

            //source encoder
            append(InputStream::class.java , StreamEncoder(arrayPool))

            //resource encoder
            val bitmapEncoder = BitmapEncoder(arrayPool)
            append(Bitmap::class.java,bitmapEncoder)


        }
    }

    fun getGlideContext(): GlideContext {
        return glideContext
    }

    fun unregisterRequestManager(requestManager: RequestManager) {

    }

    companion object {
        private var instance: KGlide? = null

        @Synchronized
        fun get(context: Context): KGlide {
            if (instance == null) {
                synchronized(KGlide::class.java) {
                    if (instance == null) {
                        checkAndInitializeGlide(context)
                    }
                }
            }
            return instance!!
        }

        fun with(activity: FragmentActivity): RequestManager {
            return get(activity).requestManagerRetriever.get(activity)
        }

        private fun checkAndInitializeGlide(context: Context) {
            val builder = KGlideBuilder()
            instance = builder.build(context)
        }
    }


    /** Creates a new instance of [RequestOptions].  */
    interface RequestOptionsFactory {
        /** Returns a non-null [RequestOptions] object.  */
        fun build(): RequestOptions
    }
}