package com.liyaan.glide.load.data

interface DataRewinder<T> {
    interface Factory<T>{
        fun build(data :T):DataRewinder<T>
        fun getDataClass(): Class<T>
    }
    fun rewindAndGet():T
    fun cleanup()
}