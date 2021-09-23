package com.liyaan.mvvm.net.utils

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GlideConfig : AppGlideModule() {
    //禁止解析Manifest
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

}