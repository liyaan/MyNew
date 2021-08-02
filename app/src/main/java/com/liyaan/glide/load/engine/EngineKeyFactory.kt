package com.liyaan.glide.load.engine

import com.liyaan.glide.load.Key
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.Transformation

class EngineKeyFactory {

    fun buildKey(
        model: Any,
        signature: Key,
        width: Int,
        height: Int,
        transformations: Map<Class<*>, Transformation<*>>,
        resourceClass: Class<*>,
        transcodeClass: Class<*>,
        options: Options
    ): EngineKey {
        return EngineKey(
            model,
            signature,
            width,
            height,
            transformations,
            resourceClass,
            transcodeClass,
            options
        )
    }
}