package com.liyaan.okhttp

import java.util.concurrent.*

class Dispatcher{


    companion object{
        var executorService:ExecutorService? = null
        fun executorService(): ExecutorService? {
            if (executorService==null){
                executorService = ThreadPoolExecutor(0, Int.MAX_VALUE,
                    60,TimeUnit.SECONDS,
                    SynchronousQueue(), ThreadFactory { r ->
                        val thred = Thread(r,"okkhttp")
                        thred.isDaemon = false
                        thred
                    })
            }
            return executorService
        }

    }
    fun enqueue(call:RealCall.AsyncCall){
        executorService()?.execute(call)
    }
}