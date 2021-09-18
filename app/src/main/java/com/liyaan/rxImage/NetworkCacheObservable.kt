package com.liyaan.rxImage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream
import java.net.URL
/**
 * RxImageLoader
.with(context)
.load("http://mmbiz.qpic.cn/mmbiz_png/via3iaqIEsXjVPJs0yFic6tBobapYt55RMYYfP153xM
QOKibTuRY7Tg2IdluCeyVoyEVA3k2d84DsolPjNwYyaum2A/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1")
.into(imageView);
 * */
class NetworkCacheObservable: AbstractCacheObservable() {
    override fun putImage(image: Image?) {

    }

    override fun getImage(url: String?): Image? {
        val bitmap = downloadImage(url)
        return Image(url!!,bitmap)
    }

    private fun downloadImage(url: String?):Bitmap{
        var bitmap: Bitmap? = null
        var inputStream: InputStream? = null

        try {
            val con = URL(url).openConnection()
            inputStream = con.getInputStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return bitmap!!
    }
}