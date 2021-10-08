package com.liyaan.utils

import android.util.Log
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun writeToFile(data:ByteArray,file: File){
        val fileOutputStream = FileOutputStream(file)
        try {
            fileOutputStream.write(data)
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            fileOutputStream.close()
        }
    }

    fun fileSize(file: File,sizeType:SizeType = SizeType.KB):Long{
        return try {
            val size = file.length()
            when(sizeType){
                SizeType.B -> size
                SizeType.KB -> size / 1024
                SizeType.MB -> size / 1024 / 1024
                SizeType.GB -> size / 1024 / 1024 / 1024
            }
        }catch (e:Exception){
            e.printStackTrace()
            -1
        }
    }


    enum class SizeType {
        B,
        KB,
        MB,
        GB
    }
}