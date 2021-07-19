package com.liyaan.okhttp

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class Response(inputStream: InputStream) {
    private var inputStream // Skin
            : InputStream = inputStream

//    fun setResponse(inputStream: InputStream?) {
//        this.inputStream = inputStream
//    }

    fun string(): String? {
        return convertStreamToString(inputStream)
    }

    fun convertStreamToString(`is`: InputStream?): String? {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String? = null
        try {
            while (reader.readLine().also { line = it } != null) {
                Log.i("aaaaaaaaaa",line.toString())
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}