package com.liyaan.rxJava

import android.os.Handler
import android.os.Looper
import android.os.Message


class LooperScheduler<T>(looper: Looper){
    private val handler: Handler = object:Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mSubscriber?.onNext(msg.obj as T)
        }
    }
    private var mSubscriber:Subscriber<T>? = null
    fun createWorker(subscriber: Subscriber<T>): Worker {
        mSubscriber = subscriber
        return Worker(handler)
    }
    inner class Worker(handler: Handler){
        fun sendContent(t: Any?){
            val message = Message.obtain(handler,1)
            message.obj = t
            handler.sendMessage(message)
        }
    }

}