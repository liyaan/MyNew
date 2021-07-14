package com.liyaan.rxJava

import android.os.Looper

class AndroidSchedulers {
    companion object{
        private val  andScheduler = LooperScheduler<Any>(Looper.getMainLooper())
        fun mianRun(): LooperScheduler<Any> {
            return andScheduler
        }
    }
}