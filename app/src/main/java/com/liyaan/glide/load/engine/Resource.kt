package com.liyaan.glide.load.engine

interface Resource<Z>  {
    fun getResourceClass(): Class<Z>
    fun get(): Z
    fun getSize(): Int
    fun recycle()
}