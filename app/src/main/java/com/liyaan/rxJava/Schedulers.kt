package com.liyaan.rxJava


import java.util.concurrent.Executors

object Schedulers {
    private val ioScheduler = Scheduler(Executors.newSingleThreadExecutor())
    fun io():Scheduler{
        return ioScheduler
    }
}
