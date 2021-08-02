package com.liyaan.glide.manager

import android.content.Context

interface ConnectivityMonitorFactory {
    fun build(context: Context, listener:ConnectivityMonitor.ConnectivityListener):ConnectivityMonitor
}