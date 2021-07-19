package com.liyaan.onedownload

import java.io.File

interface INetDownloadCallBack {
    fun success(apkFile: File?)
    fun failed(throwable: Throwable?)
    fun progress(progress: String?)
}