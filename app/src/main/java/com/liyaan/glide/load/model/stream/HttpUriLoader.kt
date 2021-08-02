package com.liyaan.glide.load.model.stream

import android.net.Uri
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.model.KGlideUrl
import com.liyaan.glide.load.model.ModelLoader
import com.liyaan.glide.load.model.ModelLoaderFactory
import com.liyaan.glide.load.model.MultiModelLoaderFactory
import java.io.InputStream

class HttpUriLoader(val urlLoader:ModelLoader<KGlideUrl,InputStream>) :ModelLoader<Uri , InputStream> {
    companion object{
        private  val SCHEMES = setOf("http","https")
    }
    override fun buildLoadData(
        model: Uri,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return urlLoader.buildLoadData(KGlideUrl(model.toString()),width,height,options)
    }

    override fun handles(model: Uri): Boolean {
        return SCHEMES.contains(model.scheme)
    }

    class Factory : ModelLoaderFactory<Uri,InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Uri, InputStream> {
            return HttpUriLoader(multiFactory.build(KGlideUrl::class.java,InputStream::class.java))
        }

    }
}