package com.liyaan.glide.load.model

import com.liyaan.glide.load.Key
import com.liyaan.glide.load.data.DataFetcher
import com.liyaan.glide.load.Options
interface ModelLoader<Model, Data> {

    class LoadData<Data>(val sourceKey: Key, val alternateKeys:List<Key> = emptyList(), val fetcher: DataFetcher<Data>) {

    }

    fun buildLoadData(
        model: Model,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<Data>?

    fun handles(model: Model): Boolean
}