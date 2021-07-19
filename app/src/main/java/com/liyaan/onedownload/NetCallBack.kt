package com.liyaan.onedownload

interface NetCallBack {
    fun success(response: String?)
    fun failed(throwable: Throwable?)
}