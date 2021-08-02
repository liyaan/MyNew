package com.liyaan.mynew

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.jni.JNIBitmap
import com.liyaan.jni.JNIDynamicLoad
import kotlinx.android.synthetic.main.activity_jni_demo.*

class JniDemoActivity:AppCompatActivity() {
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jni_demo)
        jni_demo_tv_str.text = "${JNIDynamicLoad().sum(1,2)}-${JNIDynamicLoad().geNativeString()}"
        image.setOnClickListener {
            val bitmap = JNIBitmap().callNativeMirrorBitmap(
                BitmapFactory.decodeResource(resources,R.mipmap.aaa)
            )
            image.setImageBitmap(bitmap)
        }

    }
    external fun stringFromJni():String
}