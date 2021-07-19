package com.liyaan.onedownload

import android.os.Handler
import android.os.Looper
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


private const val TAG = "OkHttpManager"
class OkHttpManager:INetManager {
    private var okHttpClient: OkHttpClient? = null
    private var mHandler: Handler? = null
    init {
        val builder =  OkHttpClient.Builder()
        builder.connectTimeout(14, TimeUnit.SECONDS)
        okHttpClient = builder.build()
        mHandler =  Handler(Looper.getMainLooper())
    }
    override fun get(url: String?, callBack: NetCallBack?) {
        val request = Request.Builder().url(url).build()
        val call = okHttpClient?.newCall(request)
        call?.enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                mHandler?.post {
                    if (callBack!=null){
                        callBack.failed(e)
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val rsp = response.body()?.string()
                mHandler?.post {
                    if (callBack!=null){
                        callBack.success(rsp)
                    }
                }
            }

        })
    }

    override fun download(
        url: String?,
        targetFile: File?,
        callBack: INetDownloadCallBack?,
        tag: Any?
    ) {
        if (!targetFile!!.exists()){
            targetFile.parentFile.mkdirs()
        }
        val request = Request.Builder().url(url).tag(tag).build()
        okHttpClient?.newCall(request)?.enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                mHandler?.post {
                    if (callBack!=null){
                        callBack.failed(e)
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream:InputStream? = null
                var outputStream:FileOutputStream? = null
                try {
                    inputStream = response.body()?.byteStream()
                    outputStream = FileOutputStream(targetFile)
                    val contentLength = response.body()?.contentLength()
                    var len = 0
                    var sum = 0
                    val bytes = ByteArray(1024*12)
                    while (inputStream!!.read(bytes).also { len = it } != -1) {
                        sum += len
                        outputStream.write(bytes, 0, len)
                        outputStream.flush()
                        val sumLen = sum
                        mHandler!!.post {
                            val s:Int =(sumLen * 1.0f / contentLength!! * 100).toInt()
                            callBack!!.progress("${s}%")
                        }
                    }
                    targetFile.setExecutable(true,false)
                    targetFile.setReadable(true,false)
                    targetFile.setWritable(true,false)
                    mHandler?.post {
                        callBack?.success(targetFile)
                    }
                }catch (e:Exception){
                    mHandler!!.post { callBack!!.failed(e) }
                }finally {
                    if (inputStream!=null){
                        inputStream?.close()
                    }
                    if (outputStream!=null){
                        outputStream?.close()
                    }
                }

            }

        })
    }

    override fun cancel(tag: Any?) {
        val calls = okHttpClient?.dispatcher()?.queuedCalls()
        calls?.forEach {
            if (it.equals(it.request().tag())){
                it.cancel()
            }
        }
        val runningCalls = okHttpClient?.dispatcher()?.runningCalls()
        runningCalls?.forEach {
            if(tag!!.equals(it.request().tag())){
                it.cancel()
            }
        }
    }
}