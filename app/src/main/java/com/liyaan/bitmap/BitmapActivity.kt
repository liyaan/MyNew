package com.liyaan.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.request.transition.BitmapTransitionFactory
import com.liyaan.mynew.R
import com.liyaan.utils.Bitmaps
import com.liyaan.utils.FileUtils
import kotlinx.android.synthetic.main.activity_bitmap.*
import java.io.ByteArrayOutputStream
import java.io.File

class BitmapActivity:AppCompatActivity() {
    private val mHandler = Handler{
        true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)
        btnFormAssets.setOnClickListener {
            val bytes = assets.open("pic.jpg").readBytes()
            val options = BitmapFactory.Options()
            options.inSampleSize = 2
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size,options)
            ivPic.setImageBitmap(bitmap)
            showInfo(bitmap)
        }

        btnFromDrawable.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources,R.drawable.pic)
            ivPic.setImageBitmap(null)
            ivPic.setImageBitmap(bitmap)
            showInfo(bitmap)
        }

        btnQualityCompress.setOnClickListener {
            val rawBytes = assets.open("pic.jpg").readBytes()
            val bitmap = BitmapFactory.decodeByteArray(rawBytes,0,rawBytes.size)
            ivPic.setImageBitmap(null)
            ivPic.setImageBitmap(bitmap)

            val bytes = Bitmaps.compressQuality(bitmap,100)
            val compressedBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)

            val compressedFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"${System.currentTimeMillis()}.jpg")
            FileUtils.writeToFile(bytes,compressedFile)

            showCompressInfo(bitmap,compressedBitmap,compressedFile)
            mHandler.postDelayed({
                ivPic.setImageBitmap(null)
                ivPic.setImageBitmap(compressedBitmap) // 3s 后展示压缩过的图片
            },3000)

        }

        btnInSampleCompress.setOnClickListener {
            val rawByte = assets.open("pic.jpg").readBytes()
            val bitmap = BitmapFactory.decodeByteArray(rawByte,0,rawByte.size)
            ivPic.setImageBitmap(bitmap)

            val compressedBytes = Bitmaps.compressInSampleSize(rawByte,500,500)
            val compressedBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)

            val compressedFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "${System.currentTimeMillis()}.jpg")
            FileUtils.writeToFile(compressedBytes,compressedFile)

            showCompressInfo(bitmap,compressedBitmap,compressedFile)
            mHandler.postDelayed({
                ivPic.setImageBitmap(compressedBitmap)
            },3000)
        }

        btnScaleCompress.setOnClickListener {
            val rawBytes = assets.open("pic.jpg").readBytes()
            val bitmap = BitmapFactory.decodeByteArray(rawBytes,0,rawBytes.size)
            ivPic.setImageBitmap(bitmap)

            val compressedBitmap = Bitmaps.compressScale(bitmap,500,500)
            val compressedFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "compressedImage.jpg")

            FileUtils.writeToFile(Bitmaps.bitmapToByteArray(compressedBitmap),compressedFile)
            showCompressInfo(bitmap, compressedBitmap, compressedFile)
            mHandler.postDelayed({
                ivPic.setImageBitmap(compressedBitmap) // 5s 后展示压缩过的图片
            },3000)
        }
        btnRGB565Compress.setOnClickListener {
            val rawBytes = assets.open("pic.jpg").readBytes()
            val bitmap = BitmapFactory.decodeByteArray(rawBytes,0,rawBytes.size)
            ivPic.setImageBitmap(bitmap)

            val compressedBitmap = Bitmaps.compressRGB565(rawBytes)
            //保存压缩的图片
            val compressedFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "compressedImage.jpg")
            val baos = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val compressedBytes = baos.toByteArray()
            FileUtils.writeToFile(compressedBytes, compressedFile)

            // 展示结果
            showCompressInfo(bitmap, compressedBitmap, compressedFile)
            mHandler.postDelayed({
                ivPic.setImageBitmap(compressedBitmap) // 5s 后展示压缩过的图片
            }, 3000)
        }
    }
    private fun showInfo(bitmap: Bitmap){
        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val bitmapInfo = "图像高度：${bitmap.width} * ${bitmap.height} \n"+
                "图片格式：${bitmap.config.name} \n"+
                "占用内存大小：${Bitmaps.getMemorySize(bitmap)}kb \n"+
                "计算占用内存的大小：${Bitmaps.calculateMemorySize(bitmap)} \n"+
                "bitmap.density：${bitmap.density} \n"+
                "屏幕的density：${resources.displayMetrics.densityDpi} \n"+
                "Bitmap转换成文件大小 ：${baos.toByteArray().size/1024} bk"
        tvInfo.text = bitmapInfo
        log(bitmapInfo)
        baos.close()
    }

    private fun showCompressInfo(rawBitmap:Bitmap,compressedBitmap: Bitmap,comptressFile: File){
        val rawBaos = ByteArrayOutputStream()
        rawBitmap.compress(Bitmap.CompressFormat.JPEG,100,rawBaos)
        val compressedFileSize = FileUtils.fileSize(comptressFile)
        val bitmapInfo = "压缩前图像宽高 ： ${rawBitmap.width} * ${rawBitmap.height} \n"+
                "图片格式： ${rawBitmap.config.name}\n" +
                "占用内存大小： ${Bitmaps.getMemorySize(rawBitmap)} kb \n"+
                "计算占用内存大小： ${Bitmaps.calculateMemorySize(rawBitmap)} kb \n"+
                "bitmap.density : ${rawBitmap.density} \n"+
                "文件大小: ${rawBaos.toByteArray().size / 1024} kb \n"+
                "\n"+
                "压缩后图像宽高 ： ${compressedBitmap.width} * ${compressedBitmap.height} \n" +
                "图片格式： ${compressedBitmap.config.name}\n" +
                "占用内存大小： ${Bitmaps.getMemorySize(compressedBitmap)} kb \n" +
                "计算占用内存大小： ${Bitmaps.calculateMemorySize(compressedBitmap)} kb \n" +
                "bitmap.density : ${compressedBitmap.density} \n" +
                "文件大小: $compressedFileSize kb "
    }

    private fun log(msg: String) {
        Log.e("tag", msg)
    }
}