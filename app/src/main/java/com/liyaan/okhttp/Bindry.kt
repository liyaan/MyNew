package com.liyaan.okhttp

import java.io.IOException
import java.io.OutputStream

interface Bindry {
    fun fileLength(): Long

    fun mimType(): String?

    fun fileName(): String?

    @Throws(IOException::class)
    fun onWrite(outputStream: OutputStream?)
}