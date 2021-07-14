package com.liyaan.rxJava

interface Transformer<T,R> {
    fun call(from:T):R
}