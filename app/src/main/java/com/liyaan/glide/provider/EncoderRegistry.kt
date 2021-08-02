package com.liyaan.glide.provider

import com.liyaan.glide.load.Encoder
import java.util.ArrayList

class EncoderRegistry {
    private val encoders = ArrayList<Entry<*>>()

    @Synchronized
    fun <Z> append(dataClass: Class<Z>, encoder: Encoder<Z>) {
        encoders.add(Entry(dataClass,encoder))
    }


    fun <X> getEncoder(dataClass: Class<X>): Encoder<X>? {
        encoders.forEach {
            if (it.handles(dataClass)){
                return it.encoder as Encoder<X>
            }
        }
        // TODO: throw an exception here?
        return null
    }

    private class Entry<T>(val resourceClass: Class<T>, val encoder: Encoder<T>) {

        fun handles(resourceClass: Class<*>): Boolean {
            return this.resourceClass.isAssignableFrom(resourceClass)
        }
    }
}