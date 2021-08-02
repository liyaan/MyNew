package com.liyaan.glide.load

import java.nio.charset.Charset
import java.security.MessageDigest

interface Key {
    companion object{
        var STRING_CHARSET_NAME = "UTF-8"
        var CHARSET =
            Charset.forName(STRING_CHARSET_NAME)
    }

    fun updateDiskCacheKey(messageDigest: MessageDigest)

    override fun equals(o: Any?): Boolean

    override fun hashCode(): Int
}