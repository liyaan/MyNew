package com.liyaan.rxJava


interface OnSubscribe<T> {
    fun call(subscriber: Subscriber<in T>)
}