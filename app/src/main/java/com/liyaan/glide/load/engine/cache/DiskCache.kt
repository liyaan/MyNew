package com.liyaan.glide.load.engine.cache

import com.liyaan.glide.load.Key
import java.io.File

interface DiskCache {
    interface Factory {
        fun build(): DiskCache?

        companion object {
            /** 250 MB of cache.  */
            const val DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024
            const val DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache"
        }
    }

    interface Writer {
        fun write(file: File): Boolean
    }

    operator fun get(key: Key): File?
    fun put(key: Key?, writer: Writer)
    fun delete(key: Key)
    fun clear()
}