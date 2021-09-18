package com.liyaan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.ref.SoftReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageManager {


    private val executorService:ExecutorService = Executors.newFixedThreadPool(5)

    private val imageCache: LruCache<String, SoftReference<Bitmap>> = LruCache<String, SoftReference<Bitmap>>(
        1024 * 1024 * 5)

    private val handler = Handler()

    companion object{
        private var mContext:Context? = null
        val instance:ImageManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            ImageManager()
        }

        fun with(context: Context):ImageManager{
            this.mContext = context
            return instance
        }

        /***
         * 加载图片的url地址，返回RequestCreator对象
         *
         * @param url
         * @return
         */

    }
    fun load(url:String):RequestCreatorRunnble {

        return ImageManager().RequestCreatorRunnble(url)
    }
    inner class RequestCreatorRunnble(url:String):Runnable{
        val url: String = url
        var holderResId = 0
        var errorResId = 0
        var imageView: ImageView? = null

        fun placeholder(holderResId: Int): RequestCreatorRunnble? {
            this.holderResId = holderResId
            return this
        }

        fun error(errorResId: Int): RequestCreatorRunnble? {
            this.errorResId = errorResId
            return this
        }
        fun into(imageView:ImageView){

            // 变成全局的
            this.imageView = imageView;
            // 一进来先设置占位图片
            if (holderResId!=0){
                imageView.setImageResource(holderResId)
            }
            // 1.去内存之中在找，有就显示，没有就往下走
            val reference = imageCache.get(url);
            var cacheBitmap:Bitmap?
            if (reference != null) {
                cacheBitmap = reference.get();
                // 有图片就显示图片
                imageView.setImageBitmap(cacheBitmap);
                return;
            }

            // 2.去本地硬盘中找，有就显示，没有就继续往下走
            // 将文件转换成bitmap对象
            val diskBitmap = getBitmapFile();
            if (diskBitmap != null) {
                // 本地磁盘有就显示图片
                imageView.setImageBitmap(diskBitmap);
                // 保存到内存中去
                imageCache.put(url, SoftReference<Bitmap>(diskBitmap));
                return;
            }
            Log.i("url",url)
            // 3.连接网络请求数据
            // 前面两步都没有的话就去联网加载数据
            // 将从网络上获取的数据放到线程池去执行

            executorService.submit(this);

        }
        override fun run() {
            var loadUrl: URL
            try {
                loadUrl = URL(url)
                val conn =loadUrl
                        .openConnection() as HttpURLConnection
                // 设置请求方式
                conn.requestMethod = "GET";
                // 设置请求时间
                conn.connectTimeout = 2000;
                Log.i("url",url)
                if (conn.responseCode == 200) {
                    val input = conn.inputStream;

                    // 获取到图片进行显示
                    val bm = BitmapFactory.decodeStream(input);

                    handler.post { // 主线程
                        imageView?.setImageBitmap(bm);
                    }

                    // 3.1保存到内存中
                    imageCache.put(url,SoftReference<Bitmap>(bm))
                    // 3.2保存到磁盘
                    // 从url中获取文件名字
                    val fileName = url.substring(url.lastIndexOf("/") + 1)

                    // 获取存储路径
                    val file = File(getCacheDir(),
                        MD5Util.encodeMd5(fileName))
                    val os = FileOutputStream(file)
                    // 将图片转换为文件进行存储
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, os)
                } else {
                    // 联网失败，显示失败图片
                    showError();
                }

            } catch (e:Exception) {
                e.printStackTrace();
                // 显示错误的图片
                showError();
            }
        }


        /***
         * 获取文件中的图片
         *
         * @return
         */
        private fun getBitmapFile():Bitmap?{
            // 从url中获取文件名字
            val fileName = url.substring(url.lastIndexOf("/") + 1)
            val file = File(getCacheDir(), MD5Util.encodeMd5(fileName))
            // 确保路径没有问题
            if (file.exists() && file.length() > 0) {
                // 返回图片
                return BitmapFactory.decodeFile(file.getAbsolutePath())

            } else {

                return null
            }
        }

        /***
         * 显示错误的图片
         */
        private fun showError() {
            handler.post { imageView?.setImageResource(errorResId) };
        }

        /***
         * 读取缓存路径目录
         *
         * @return
         */
        private fun getCacheDir():File {
            // 获取保存的文件夹路径
            var file:File?
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                // 有SD卡就保存到SD卡
                file = mContext?.externalCacheDir;
            } else {
                // 没有就保存到内部存储
                file = mContext?.cacheDir;
            }
            return file!!
        }
    }
}