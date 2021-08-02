package com.liyaan.glide.load

import android.content.Context
import com.liyaan.glide.load.engine.Resource

interface Transformation<T> :Key {
    fun transform(
        context: Context, resource: Resource<T>, outWidth: Int, outHeight: Int
    ): Resource<T>?
}