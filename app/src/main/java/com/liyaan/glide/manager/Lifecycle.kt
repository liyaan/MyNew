package com.liyaan.glide.manager

interface Lifecycle {
    fun addListener(listener :LifecycleListener)
    fun removeListener(listener :LifecycleListener)
}