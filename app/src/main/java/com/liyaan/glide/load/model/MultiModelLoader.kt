package com.liyaan.glide.load.model

import androidx.core.util.Pools
import com.liyaan.glide.load.Options
class MultiModelLoader<Model, Data>(
    val modelLoaders: List<ModelLoader<Model, Data>>,
    val exceptionListPool: Pools.Pool<List<Throwable>>
) : ModelLoader<Model, Data> {
    override fun buildLoadData(
        model: Model,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Data>? {
        return modelLoaders[0].buildLoadData(
            model, width,
            height,
            options
        )
    }

    override fun handles(model: Model): Boolean {
        for (modelLoader in modelLoaders) {
            if (modelLoader.handles(model)) {
                return true
            }
        }
        return false
    }
}