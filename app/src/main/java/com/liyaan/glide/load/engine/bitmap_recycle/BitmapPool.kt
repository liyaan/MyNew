package com.liyaan.glide.load.engine.bitmap_recycle

import android.graphics.Bitmap

interface BitmapPool {
    fun put(bitmap: Bitmap)
}