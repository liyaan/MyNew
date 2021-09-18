package com.liyaan.rxImage

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.security.MessageDigest

object DiskCacheUtil {

    fun getDiskCacheDir(context: Context,uniqueName:String):File{
        var cachePath:String
        cachePath = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            || !Environment.isExternalStorageEmulated()){
            context.cacheDir.path
        }else{
            context.cacheDir.path
        }
        Log.i("DiskCacheUtil",cachePath)
        return File(cachePath+File.separator+uniqueName)
    }

    fun getAppVersionCode(context: Context):Int{
        val info = context.packageManager.getPackageInfo(context.packageName,0)
        return info.versionCode
    }

    fun getMd5String(key:String):String{
        var cacheKey:String
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(key.toByteArray())
        cacheKey = bytesToHexString(messageDigest.digest())
        return cacheKey
    }

    private fun bytesToHexString(bytes:ByteArray):String{
        return bytesToHexString(bytes)
    }
}