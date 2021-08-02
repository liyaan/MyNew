package com.liyaan.glide.request

import com.liyaan.glide.load.DataSource
import com.liyaan.glide.load.engine.Resource

interface ResourceCallback {
    fun onResourceReady(resource: Resource<*>, dataSource: DataSource?)
    fun onLoadFailed(e: Exception)
    fun getLock(): Any
}