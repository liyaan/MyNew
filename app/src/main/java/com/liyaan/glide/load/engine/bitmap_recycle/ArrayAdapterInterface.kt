package com.liyaan.glide.load.engine.bitmap_recycle

interface ArrayAdapterInterface<T> {
    fun getTag(): String
    fun getArrayLength(array: T): Int
    fun newArray(length: Int): T
    fun getElementSizeInBytes(): Int
}