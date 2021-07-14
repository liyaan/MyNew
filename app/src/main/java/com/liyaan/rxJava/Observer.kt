package com.liyaan.rxJava

interface Observer<T> {
    fun onCompleted()
    fun onError(t:Throwable)
    fun onNext(t:T)
}