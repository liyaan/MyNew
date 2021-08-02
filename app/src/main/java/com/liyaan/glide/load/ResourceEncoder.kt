package com.liyaan.glide.load

import com.liyaan.glide.load.engine.Resource

interface ResourceEncoder<T> :Encoder<Resource<T>>{
    fun getEncodeStrategy(options: Options):EncodeStrategy
}