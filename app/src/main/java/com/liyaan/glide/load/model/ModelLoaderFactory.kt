package com.liyaan.glide.load.model

interface ModelLoaderFactory<T, Y> {
    fun build(multiFactory:MultiModelLoaderFactory):ModelLoader<T, Y>
}