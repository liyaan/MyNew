package com.liyaan.download

import android.os.AsyncTask
import android.os.Environment
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile


const val TYPE_SUCCESS = 0
const val TYPE_FAILED = 1
const val TYPE_PAUSED = 2
const val TYPE_CANCELED = 3
class DownloadTask(listener: DownloadListener): AsyncTask<String, Int, Int>() {
    private var listener: DownloadListener? = listener
    private var isCanceled = false
    private var isPaused = false
    private var lastProgress = 0

    override fun doInBackground(vararg params: String?): Int {
        var `is`: InputStream? = null
        var saveFile: RandomAccessFile? = null
        var file: File? = null
        try {
            var downloadLength: Long = 0
            val downloadUrl = params[0]!!
            val filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"))
            val directory: String =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getPath()
            file = File(directory + filename)
            if (file.exists()) {
                downloadLength = file.length()
            }
            val contentLength: Long = getContentLength(downloadUrl)
            if (contentLength == 0L) {
                return TYPE_FAILED
            } else if (contentLength == downloadLength) {
                return TYPE_SUCCESS
            }
            val client = OkHttpClient()
            val request: Request = Request.Builder() //断点下载，指定从哪个字节开始下载
                .addHeader("RANGE", "bytes=$downloadLength-")
                .url(downloadUrl)
                .build()
            val response: Response = client.newCall(request).execute()
            if (response != null) {
                `is` = response.body()!!.byteStream()
                saveFile = RandomAccessFile(file, "rw")
                saveFile.seek(downloadLength) //跳过已下载的字节
                val b = ByteArray(1024)
                var total = 0
                var len: Int = 0
                while (`is`.read(b).also({ len = it }) != -1) {
                    total += if (isCanceled) {
                        return TYPE_CANCELED
                    } else if (isPaused) {
                        return TYPE_PAUSED
                    } else {
                        len
                    }
                    saveFile.write(b, 0, len)
                    //计算已经下载的百分比
                    val progress = ((total + downloadLength) * 100 / contentLength).toInt()
                    publishProgress(progress)
                }
                response.body()!!.close()
                return TYPE_SUCCESS
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (`is` != null) {
                    `is`.close()
                }
                if (saveFile != null) {
                    saveFile.close()
                }
                if (isCanceled && file != null) {
                    file.delete()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return TYPE_FAILED
    }
    override fun onProgressUpdate(vararg values: Int?) {
        val progress:Int = values[0]!!
        if (progress > lastProgress) {
            listener!!.onProgress(progress)
            lastProgress = progress
        }
    }

    override fun onPostExecute(status: Int?) {
        when (status) {
            TYPE_SUCCESS -> listener!!.onSuccess()
            TYPE_FAILED -> listener!!.onFail()
            TYPE_PAUSED -> listener!!.onPaused()
            TYPE_CANCELED -> listener!!.onCanceled()
            else -> {
            }
        }
    }

    fun pauseDownload() {
        isPaused = true
    }

    fun cancelDownload() {
        isCanceled = true
    }

    /**
     * 获取待下载文件的总长度
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun getContentLength(downloadUrl: String): Long {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(downloadUrl)
            .build()
        val response: Response = client.newCall(request).execute()
        if (response != null && response.isSuccessful()) {
            val contentLength: Long = response.body()!!.contentLength()
            response.close()
            return contentLength
        }
        return 0
    }
}