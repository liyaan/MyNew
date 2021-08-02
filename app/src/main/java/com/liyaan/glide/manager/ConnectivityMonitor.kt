package com.liyaan.glide.manager

interface ConnectivityMonitor:LifecycleListener {

    interface ConnectivityListener{
        fun onConnectivityChanged(isConnected :Boolean)
    }
}