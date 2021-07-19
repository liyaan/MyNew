package com.liyaan.okhttp

abstract class NamedRunnable: Runnable {
    override fun run() {
        execute()
    }
    protected abstract fun execute()
}