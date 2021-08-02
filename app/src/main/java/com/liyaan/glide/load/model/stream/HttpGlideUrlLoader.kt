package com.liyaan.glide.load.model.stream

import com.liyaan.glide.load.Options
import com.liyaan.glide.load.data.HttpUrlFetcher
import com.liyaan.glide.load.model.KGlideUrl
import com.liyaan.glide.load.model.ModelLoader
import com.liyaan.glide.load.model.ModelLoaderFactory
import com.liyaan.glide.load.model.MultiModelLoaderFactory
import java.io.InputStream

class HttpGlideUrlLoader :ModelLoader<KGlideUrl,InputStream> {
    override fun buildLoadData(
        model: KGlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(model,fetcher = HttpUrlFetcher(model))
    }

    override fun handles(model: KGlideUrl): Boolean {
        return true
    }

    class Factory :ModelLoaderFactory<KGlideUrl ,InputStream>{
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<KGlideUrl, InputStream> {
            return HttpGlideUrlLoader()
        }

    }
}