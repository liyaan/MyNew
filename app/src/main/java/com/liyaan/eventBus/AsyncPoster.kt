package com.liyaan.eventBus

import java.util.concurrent.Executors

class AsyncPoster(subscription: Subscription,obj:Any):Runnable {
    val subscription = subscription
    val event = obj
    companion object{
        @JvmStatic
        val EXECUTOR_SERVICE = Executors.newCachedThreadPool()
        @JvmStatic
        fun enqueue(subscription: Subscription,event:Any){
            val asyPoster = AsyncPoster(subscription,event)
            EXECUTOR_SERVICE.execute(asyPoster)
        }
    }
    override fun run() {
        subscription.subsciberMethod.method.invoke(subscription.subscriber,event)
    }
}