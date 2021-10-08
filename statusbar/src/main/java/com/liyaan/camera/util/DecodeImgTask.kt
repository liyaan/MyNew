package com.liyaan.camera.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.lang.ref.WeakReference

class DecodeImgTask(imageView: ImageView) : AsyncTask<String, Int, Bitmap?>() {

    private val imageViewReference: WeakReference<ImageView> = WeakReference(imageView)
    private var temp = 0L
    override fun doInBackground(vararg params: String?): Bitmap? {
        temp = System.currentTimeMillis()
        return BitmapFactory.decodeFile(params[0])
    }

    override fun onPostExecute(result: Bitmap?) {
        if (imageViewReference!=null && result!=null){
            imageViewReference.get()?.let {
                val compressBitmap = BitmapUtils.decodeBitmap(result,720,1080)
                it.setImageBitmap(compressBitmap)
            }
        }
        super.onPostExecute(result)
    }
}