package com.liyaan.glide.load.engine

import com.liyaan.glide.load.Key
import com.liyaan.glide.load.Options
import com.liyaan.glide.load.Transformation
import java.security.MessageDigest

class EngineKey(
    val model: Any,
    val signature: Key,
    val width: Int,
    val height: Int,
    val transformations: Map<Class<*>, Transformation<*>>,
    val resourceClass: Class<*>,
    val transcodeClass: Class<*>,
    val options: Options
) : Key {
    private var hashCode = 0
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        throw UnsupportedOperationException()
    }

    override fun equals(o: Any?): Boolean {
        if (o is EngineKey) {
            return (model == o.model && signature.equals(o.signature)
                    && height == o.height && width == o.width && transformations == o.transformations && resourceClass == o.resourceClass && transcodeClass == o.transcodeClass && options.equals(
                o.options
            ))
        }
        return false
    }

    override fun hashCode(): Int {
        if (hashCode == 0) {
            hashCode = model.hashCode()
            hashCode = 31 * hashCode + signature.hashCode()
            hashCode = 31 * hashCode + width
            hashCode = 31 * hashCode + height
            hashCode = 31 * hashCode + transformations.hashCode()
            hashCode = 31 * hashCode + resourceClass.hashCode()
            hashCode = 31 * hashCode + transcodeClass.hashCode()
            hashCode = 31 * hashCode + options.hashCode()
        }
        return hashCode
    }
}