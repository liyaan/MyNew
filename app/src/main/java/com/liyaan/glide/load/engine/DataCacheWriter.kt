package com.liyaan.glide.load.engine

import com.liyaan.glide.load.Encoder
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.engine.cache.DiskCache
import java.io.File

class DataCacheWriter<DataType>(val encoder:Encoder<DataType> , val data:DataType ,val options :Options): DiskCache.Writer {
    override fun write(file: File): Boolean {
        return encoder.encode(data, file, options)
    }
}