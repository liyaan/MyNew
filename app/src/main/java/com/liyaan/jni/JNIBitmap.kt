package com.liyaan.jni

import android.graphics.Bitmap

class JNIBitmap {
    external fun callNativeMirrorBitmap(bitmap:Bitmap):Bitmap
}