package com.liyaan.onedownload

import java.io.File

interface INetManager{
    fun get(url: String, callBack: NetCallBack?)
    fun download(
        url: String,
        targetFile: File?,
        callBack: INetDownloadCallBack?,
        tag: Any?
    )

    fun cancel(tag: Any?)
}