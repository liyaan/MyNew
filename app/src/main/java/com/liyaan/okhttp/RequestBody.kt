package com.liyaan.okhttp

import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.net.URLConnection
import java.util.*
import kotlin.collections.HashMap

// 表单格式

class RequestBody() {
    companion object{
        const val FORM = "multipart/form-data"
    }
    private var params:HashMap<String,Any>? = null
    private var boundary = createBoundary()
    private var type:String? = null
    private var startBoundary:String = "--$boundary"
    private var endBoundary = "$startBoundary--" //结束的边界

    init {
        params = HashMap()
    }

    private fun createBoundary(): String? {
        return "OkHttp" + UUID.randomUUID().toString()
    }

    fun getContentType():String{
        return "$type;boundary=$boundary"
    }
    fun getContentLength():Long{
        var length:Long = 0
        val entries = params?.entries
        entries?.forEach{
            val key = it.key
            val value = it.value
            if (value is String){
                val text:String = getText(key,value)
                length+=text.toByteArray().size
            }
            if (value is Bindry){
                val bindry = value as Bindry
                val text = getText(key,bindry)
                length += text.toByteArray().size.toLong()
                length += bindry.fileLength() + "\r\n".toByteArray().size
            }
        }
        if(params!!.isNotEmpty()){
            length += endBoundary.toByteArray().size.toLong()
        }
        return length
    }
    fun onWriteBody(outputStream:OutputStream){
        val entries = params?.entries
        entries?.forEach {
            val key:String = it.key
            val value = it.value
            if (value is String){
                val text = getText(key,value as String)
                outputStream.write(text.toByteArray())
            }
            if (value is Bindry){
                val bindry = value as Bindry
                val text = getText(key,bindry)
                outputStream.write(text.toByteArray())
                bindry.onWrite(outputStream)
                outputStream.write("\r\n".toByteArray())
            }
        }
        if (params?.size!=0){
            outputStream.write(endBoundary.toByteArray())
        }
    }



    private fun getText(key: String, value: String): String {
        return "$startBoundary\r\nContent-Disposition: form-data; name = \"$key\"\r\nContext-Type: text/plain\r\n\r\n$value\r\n"
    }
    private fun getText(key: String, value: Bindry): String {
        return "$startBoundary\r\nContent-Disposition: form-data; name = \""+key+"\" filename = \""+value.fileName()+"\"Context-Type: "+value.mimType()+"\r\n\r\n"
    }

    fun addParam(key:String,value:String):RequestBody{
        params?.put(key,value)
        return this
    }
    fun type(type:String):RequestBody{
        this.type = type
        return this
    }
    fun addParam(key:String,value:Bindry):RequestBody{
        params?.put(key,value)
        return this
    }

    fun create(file:File):Bindry{
        return object:Bindry{
            override fun fileLength(): Long {
                return file.length()
            }

            override fun mimType(): String? {
                val fileNameMap = URLConnection.getFileNameMap()
                val mimType:String = fileNameMap.getContentTypeFor(file.absolutePath)
                if (TextUtils.isEmpty(mimType)){
                    return "application/octet-stream"
                }
                return mimType
            }

            override fun fileName(): String? {
                return file.getName()
            }

            override fun onWrite(outputStream: OutputStream?) {
                val inputStream = FileInputStream(file)
                val buffer = ByteArray(2048)
                var len = 0
                while (inputStream.read().also { len = it }!=-1){
                    outputStream?.write(buffer,0,len)
                }
                inputStream.close()
            }

        }
    }
}