package com.liyaan.glide.load.engine.resource.transcode

import com.liyaan.glide.load.Options
import com.liyaan.glide.load.engine.Resource

interface ResourceTranscoder<Z, R>  {
    fun transcode(toTranscode: Resource<Z>?, options : Options):Resource<R>
}