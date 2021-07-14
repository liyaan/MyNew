package com.liyaan.rxJava

import java.util.concurrent.Executor

open class Scheduler(executor:Executor) {
    val executor = executor

    fun createWorker():Worker{
        return Worker(executor)
    }
    inner class Worker(executor:Executor){
        fun schedule(runnable: Runnable){
            executor.execute(runnable)
        }
    }
}