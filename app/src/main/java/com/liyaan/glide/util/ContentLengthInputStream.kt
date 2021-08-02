package com.liyaan.glide.util

import android.text.TextUtils
import android.util.Log
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream

class ContentLengthInputStream(other:InputStream,contentLength:Long): FilterInputStream(other) {


    private val contentLength: Long = contentLength
    private var readSoFar = 0

    companion object{
        private val TAG = "ContentLengthStream"
        private val UNKNOWN = -1
        fun obtain(
            other: InputStream, contentLengthHeader: String?
        ): InputStream {
            return obtain(other,parseContentLength(contentLengthHeader).toLong())
        }

        fun obtain(other: InputStream, contentLength: Long): InputStream {
            return ContentLengthInputStream(other, contentLength)
        }
        private fun parseContentLength(contentLengthHeader: String?): Int {
            var result = UNKNOWN
            if (!TextUtils.isEmpty(contentLengthHeader)) {
                try {
                    result = contentLengthHeader!!.toInt()
                } catch (e: NumberFormatException) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(
                            TAG,
                            "failed to parse content length header: $contentLengthHeader",
                            e
                        )
                    }
                }
            }
            return result
        }
    }



    @Synchronized
    @Throws(IOException::class)
    override fun available(): Int {
        return Math.max(contentLength - readSoFar, `in`.available().toLong()).toInt()
    }

    @Synchronized
    @Throws(IOException::class)
    override fun read(): Int {
        val value = super.read()
        checkReadSoFarOrThrow(if (value >= 0) 1 else -1)
        return value
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray): Int {
        return read(buffer, 0 /*byteOffset*/, buffer.size /*byteCount*/)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun read(buffer: ByteArray?, byteOffset: Int, byteCount: Int): Int {
        return checkReadSoFarOrThrow(super.read(buffer, byteOffset, byteCount))
    }

    @Throws(IOException::class)
    private fun checkReadSoFarOrThrow(read: Int): Int {
        if (read >= 0) {
            readSoFar += read
        } else if (contentLength - readSoFar > 0) {
            throw IOException(
                "Failed to read all expected data"
                        + ", expected: "
                        + contentLength
                        + ", but read: "
                        + readSoFar
            )
        }
        return read
    }
}