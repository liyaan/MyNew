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
                ivPic.setImageBitmap(compressedBitmap) // 3s ???????????????????????????
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
                ivPic.setImageBitmap(compressedBitmap) // 5s ???????????????????????????
            },3000)
        }
        btnRGB565Compress.setOnClickListener {
            val rawBytes = assets.open("pic.jpg").readBytes()
            val bitmap = BitmapFactory.decodeByteArray(rawBytes,0,rawBytes.size)
            ivPic.setImageBitmap(bitmap)

            val compressedBitmap = Bitmaps.compressRGB565(rawBytes)
            //?????????????????????
            val compressedFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "compressedImage.jpg")
            val baos = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val compressedBytes = baos.toByteArray()
            FileUtils.writeToFile(compressedBytes, compressedFile)

            // ????????????
            showCompressInfo(bitmap, compressedBitmap, compressedFile)
            mHandler.postDelayed({
                ivPic.setImageBitmap(compressedBitmap) // 5s ???????????????????????????
            }, 3000)
        }
    }
    private fun showInfo(bitmap: Bitmap){
        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val bitmapInfo = "???????????????${bitmap.width} * ${bitmap.height} \n"+
                "???????????????${bitmap.config.name} \n"+
                "?????????????????????${Bitmaps.getMemorySize(bitmap)}kb \n"+
                "??????????????????????????????${Bitmaps.calculateMemorySize(bitmap)} \n"+
                "bitmap.density???${bitmap.density} \n"+
                "?????????density???${resources.displayMetrics.densityDpi} \n"+
                "Bitmap????????????????????? ???${baos.toByteArray().size/1024} bk"
        tvInfo.text = bitmapInfo
        log(bitmapInfo)
        baos.close()
    }

    private fun showCompressInfo(rawBitmap:Bitmap,compressedBitmap: Bitmap,comptressFile: File){
        val rawBaos = ByteArrayOutputStream()
        rawBitmap.compress(Bitmap.CompressFormat.JPEG,100,rawBaos)
        val compressedFileSize = FileUtils.fileSize(comptressFile)
        val bitmapInfo = "????????????????????? ??? ${rawBitmap.width} * ${rawBitmap.height} \n"+
                "??????????????? ${rawBitmap.config.name}\n" +
                "????????????????????? ${Bitmaps.getMemorySize(rawBitmap)} kb \n"+
                "??????????????????????????? ${Bitmaps.calculateMemorySize(rawBitmap)} kb \n"+
                "bitmap.density : ${rawBitmap.density} \n"+
                "????????????: ${rawBaos.toByteArray().size / 1024} kb \n"+
                "\n"+
                "????????????????????? ??? ${compressedBitmap.width} * ${compressedBitmap.height} \n" +
                "??????????????? ${compressedBitmap.config.name}\n" +
                "????????????????????? ${Bitmaps.getMemorySize(compressedBitmap)} kb \n" +
                "??????????????????????????? ${Bitmaps.calculateMemorySize(compressedBitmap)} kb \n" +
                "bitmap.density : ${compressedBitmap.density} \n" +
                "????????????: $compressedFileSize kb "
    }

    private fun log(msg: String) {
        Log.e("tag", msg)
    }
}