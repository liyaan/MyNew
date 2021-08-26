package com.liyaan.utils

import android.R.attr
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color


object BitmapUtils {
    fun bitmapSampleSize(context: Context,drawable:Int):Bitmap{
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources,drawable)
        val width = options.outWidth
        val height = options.outHeight
        val reqWidth = 200
        val reqHeight = 200
        val imageType = options.outMimeType
        var inSampleSize = 1
        if (width>reqWidth || height>reqHeight){
            val halfHeight = height/2
            val halfWidth = width/2
            while ((halfWidth/inSampleSize)>reqWidth && (halfHeight/inSampleSize)>reqHeight){
                inSampleSize = 2
            }
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(context.resources,drawable,options)
    }

    fun bitmapGray(bitmap: Bitmap,schema:Int):Bitmap{
        val bm = Bitmap.createBitmap(bitmap.width,bitmap.height,bitmap.config)
        val width = bm.width
        val height = bm.height
        for (row in 0 until height){
            for (col in 0 until width){
                val pixel: Int = bitmap.getPixel(col, row) // ARGB

                val red = Color.red(pixel) // same as (pixel >> 16) &0xff

                val green = Color.green(pixel) // same as (pixel >> 8) &0xff

                val blue = Color.blue(pixel) // same as (pixel & 0xff)

                val alpha = Color.alpha(pixel) // same as (pixel >>> 24)

                var gray = 0
                if (schema === 0) {
                    gray = (Math.max(blue, Math.max(red, green)) +
                            Math.min(blue, Math.min(red, green))) / 2
                } else if (schema === 1) {
                    gray = (red + green + blue) / 3
                } else if (schema === 2) {
                    gray = (0.3 * red + 0.59 * green + 0.11 * blue).toInt()
                }
                bm.setPixel(col, row, Color.argb(alpha, gray, gray, gray))
            }
        }
        return bm
    }
    fun brightness(bitmap:Bitmap,depth:Double):Bitmap{
        val bm = Bitmap.createBitmap(bitmap.width,bitmap.height,bitmap.config)
        val width = bm.width
        val height = bm.height
        for(row in 0 until height){
            for (col in 0 until width){
                val pixel: Int = bitmap.getPixel(col, row) // ARGB

                var red = Color.red(pixel) // same as (pixel >> 16) &0xff

                var green = Color.green(pixel) // same as (pixel >> 8) &0xff

                var blue = Color.blue(pixel) // same as (pixel & 0xff)

                val alpha = Color.alpha(pixel) // same as (pixel >>> 24)

                val gray = 0.3 * red + 0.59 * green + 0.11 * blue
                red += (depth * gray).toInt()
                if (red > 255) {
                    red = 255
                }

                green += (depth * gray).toInt()
                if (green > 255) {
                    green = 255
                }

                blue += (depth * gray).toInt()
                if (blue > 255) {
                    blue = 255
                }
                bm.setPixel(col, row, Color.argb(alpha, red, green, blue))
            }
        }
        return bm
    }
    fun flip(bitmap:Bitmap):Bitmap{
        val bm = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            bitmap.config
        )
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        for (row in 0 until height) {
            for (col in 0 until width) {
                val pixel: Int = bitmap.getPixel(col, row) // ARGB
                val red = Color.red(pixel) // same as (pixel >> 16) &0xff
                val green = Color.green(pixel) // same as (pixel >> 8) &0xff
                val blue = Color.blue(pixel) // same as (pixel & 0xff)
                val alpha = Color.alpha(pixel) // same as (pixel >>> 24)
                val ncol = width - col - 1
                bm.setPixel(ncol, row, Color.argb(alpha, red, green, blue))
            }
        }
        return bm
    }
}