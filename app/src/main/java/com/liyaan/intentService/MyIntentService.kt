package com.liyaan.intentService

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.os.Message
import android.util.Log
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MyIntentService : IntentService {

    constructor():super("MyIntentService")
    companion object{
        var updateUI: UpdateUI? = null

    }

    override fun onHandleIntent(intent: Intent?) {
        val bitmap = intent?.getStringExtra("download_url")?.let {
            downloadUrlBitmap(it)
        }
        val msg = Message()
        msg.what = intent?.getIntExtra("index_flag",0)?:0
        msg.obj = bitmap
        updateUI?.updateMsgUI(msg)
    }
    override fun onCreate() {
        super.onCreate()
        Log.i("aaaa","onCreate")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.i("aaaa","onStart")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("aaaa","onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.i("aaaa","onDestroy")
        super.onDestroy()

    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("aaaa","onBind")
        return super.onBind(intent)
    }

    interface UpdateUI{
        fun updateMsgUI(message: Message)
    }

    private fun downloadUrlBitmap(imgUrl:String):Bitmap?{
        var urlConnection: HttpURLConnection? = null
        var input: BufferedInputStream? = null
        var bitmap: Bitmap? = null
        try{
            val url = URL(imgUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            input = BufferedInputStream(urlConnection.inputStream,8*1024)
            bitmap = BitmapFactory.decodeStream(input)
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            urlConnection?.disconnect()
            try {
                input?.close()
            } catch ( e: IOException) {
                e.printStackTrace();
            }
        }
        return bitmap?:null
    }
}