package com.liyaan.glide.load.key

import com.liyaan.glide.load.Key
import com.liyaan.glide.load.engine.EngineJob
import com.liyaan.glide.load.engine.EngineResource

interface EngineJobListener {
    fun onEngineJobComplete(
        engineJob: EngineJob<*>?,
        key: Key?,
        resource: EngineResource<*>?
    )

    fun onEngineJobCancelled(engineJob: EngineJob<*>?, key: Key?)
}