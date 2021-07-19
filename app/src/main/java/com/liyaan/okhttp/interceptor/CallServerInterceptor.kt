package com.liyaan.okhttp.interceptor

import android.util.Log
import com.liyaan.okhttp.RequestBody
import com.liyaan.okhttp.Response
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CallServerInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val url = URL(request?.url())
        var urlConnection =  url.openConnection() as HttpURLConnection
        if (urlConnection is HttpsURLConnection){
            urlConnection = urlConnection as HttpsURLConnection
        }
        urlConnection.requestMethod = request?.method?.methodName
        urlConnection.doOutput = request?.method()?.doOutput()!!
        urlConnection.useCaches = false
        val requestBody: RequestBody? = request.requestBody()
        val hesders = request.headers
        if (hesders!=null){
            val entries = hesders.entries
            entries.forEach {
                urlConnection.setRequestProperty(it.key,it.value)
            }
        }
        if (request.jsonString!=null){
            urlConnection.setDoInput(true)
            val wr =
                DataOutputStream(urlConnection.getOutputStream())
            wr.writeBytes(request.jsonString)
            wr.flush()
            wr.close()
        }
        urlConnection.connect()

        requestBody?.onWriteBody(urlConnection.outputStream)
        val statusCode = urlConnection.responseCode
        Log.i("aaaaaa","aaaaaaaaaaaa$statusCode")
        if (statusCode==200){
            val inputStream = urlConnection.inputStream
            val response = Response(inputStream)
            return response
        }else{
            val inputStream = urlConnection.inputStream
            val response = Response(inputStream)
            return response
        }
    }
}