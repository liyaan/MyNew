package com.liyaan.utils

import android.graphics.*
import android.os.Build
import java.io.ByteArrayOutputStream

object Bitmaps {

    fun getMemorySize(bitmap:Bitmap,sizeType:SizeType=SizeType.KB):Int{
        val bytes = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            bitmap.allocationByteCount
        }else{
            bitmap.byteCount
        }
        return when(sizeType){
            SizeType.B -> bytes
            SizeType.KB -> bytes / 1024
            SizeType.MB -> bytes / 1024 / 1024
            SizeType.GB -> bytes / 1024 / 1024 / 1024
        }
    }
    fun calculateMemorySize(bitmap: Bitmap,sizeType: SizeType=SizeType.KB):Int{
        val pixels = bitmap.width*bitmap.height
        val bytes = when(bitmap.config){
            Bitmap.Config.ALPHA_8->pixels*1
            Bitmap.Config.ARGB_4444->pixels*2
            Bitmap.Config.ARGB_8888->pixels*4
            Bitmap.Config.RGB_565->pixels*2
            else->pixels*4
        }
        return when(sizeType){
            SizeType.B -> bytes
            SizeType.KB -> bytes / 1024
            SizeType.MB -> bytes / 1024 / 1024
            SizeType.GB -> bytes / 1024 / 1024 / 1024
        }
    }

    fun compressQuality(bitmap: Bitmap,targetSize:Int,declineQuaslity:Int = 100):ByteArray{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        var quality = 100
        while ((baos.toByteArray().size/1024)>targetSize){
            baos.reset()
            quality-=declineQuaslity
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality,baos)
        }
        return baos.toByteArray()
    }

    fun compressInSampleSize(byteArray: ByteArray,targetWidth:Int,targetHeight:Int):ByteArray{
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(byteArray,0,byteArray.size,options)
        var inSampleSize = 1
        while (options.outWidth/inSampleSize>targetWidth
            || options.outHeight/inSampleSize>targetHeight){
            inSampleSize *=2
        }
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size,options)
        return bitmapToByteArray(bitmap)
    }

    fun compressScale(bitmap: Bitmap,targetWidth: Int,targetHeight: Int): Bitmap {
        return try {
            val scale = Math.min(targetWidth*1.0f/bitmap.width,targetHeight*1.0f/bitmap.height)
            val matrix = Matrix()
            matrix.setScale(scale,scale)
            val scaleBitmap = Bitmap.createScaledBitmap(bitmap,(bitmap.width*scale).toInt(),(bitmap.height*scale).toInt(),true)
            val rawBytes = bitmapToByteArray(bitmap)
            val scaledBytes = bitmapToByteArray(scaleBitmap)
            scaleBitmap
        }catch(e:Exception){
            e.printStackTrace()
            bitmap
        }
    }
    fun compressRGB565(byteArray: ByteArray): Bitmap {
        return try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            val compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size,options)

            compressedBitmap
        }catch (e: Exception) {
            e.printStackTrace()
            BitmapFactory.decodeByteArray(ByteArray(0), 0, 0)
        }
    }
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        return baos.toByteArray()
    }
    /**
     * 水平镜像
     */
    fun mirrorX(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.setScale(-1f,1f)
        return Bitmap.createBitmap(bitmap,0,0,bitmap.width, bitmap.height, matrix, false)
    }
    /**
     * 竖直镜像
     */
    fun mirrorY(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.setScale(1f,-1f)
        return Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,false)
    }
    /**
     * 旋转
     *
     * 注意：如果 [degree] 不是90的倍数的话，会导致旋转后图片变成"斜的"，
     * 然而此时计算图片的宽高时仍然是按照水平和竖直方向计算，所以会导致最终旋转后的图片变大
     * 如果进行多次旋转的话，最终会出现OMM
     */
    fun rotate(bitmap: Bitmap, degree: Float): Bitmap{
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,false)
    }
    /**
     * 根据比例 [scale] 缩放
     */
    fun scale(bitmap: Bitmap, scale: Float): Bitmap{
        return if (scale==1f || scale<=0){
            bitmap
        }else{
            Bitmap.createScaledBitmap(bitmap,
                (bitmap.width*scale).toInt(),
                (bitmap.height*scale).toInt(),false)
        }
    }
    /**
     * 从图片中间位置裁剪出一个宽高为的 [width] [height]图片
     */
    fun crop(bitmap: Bitmap, width: Int, height: Int): Bitmap{
        return if (bitmap.width<width || bitmap.height<height){
            bitmap
        }else{
            Bitmap.createBitmap(bitmap,
                (bitmap.width-width)/2,(bitmap.height-height)/2,width, height)
        }
    }
    /**
     * 从图片中间位置裁剪出一个半径为 [radius] 的圆形图片
     */
    fun cropCircle(bitmap: Bitmap, radius: Int): Bitmap{
        val realRadius = if (bitmap.width/2<radius || bitmap.height/2<radius){
            Math.min(bitmap.width,bitmap.height)/2
        }else{
            radius
        }
        val src = crop(bitmap,realRadius*2,realRadius*2)
        val circle = Bitmap.createBitmap(src.width,src.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circle)
        canvas.drawARGB(0,0,0,0)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawCircle((circle.width/2).toFloat(),(circle.height/2).toFloat(),realRadius.toFloat(),paint)
        val rect = Rect(0,0,circle.width,circle.height)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src,rect,rect,paint)
        return circle
    }
    /**
     * 根据指定宽 [newWidth] 、高 [newHeight] 进行缩放
     */
    fun scale(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return if(newWidth<=0 && newHeight<=0){
            bitmap
        }else{
            val scaleX = newWidth.toFloat()/bitmap.width
            val scaleY = newHeight.toFloat()/bitmap.height
            val matrix = Matrix()
            matrix.setScale(scaleX,scaleY)
            Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,false)
        }
    }
    enum class SizeType {
        B,
        KB,
        MB,
        GB
    }
}