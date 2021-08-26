package com.liyaan.mynew

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.annotation.BindView
import com.liyaan.api.ButterKnife
import com.liyaan.jni.JNIBitmap
import com.liyaan.jni.JNIDynamicLoad
import com.liyaan.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_jni_demo.*

class JniDemoActivity:AppCompatActivity() {
    private var bitmap:Bitmap?=null
    @BindView(R.id.bind_demo_tv_str)
    lateinit var bind_demo_tv_str:TextView
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jni_demo)
        ButterKnife.bind(this)
        bitmap = BitmapFactory.decodeResource(resources,R.mipmap.aaa)
        bind_demo_tv_str.text = "bind_demo_tv_str"
        jni_demo_tv_str.text = "${JNIDynamicLoad().sum(1,2)}-${JNIDynamicLoad().geNativeString()}"
        image.setOnClickListener {
            val bitmap = JNIBitmap().callNativeMirrorBitmap(
                bitmap!!
            )
            image.setImageBitmap(bitmap)
        }
        button_gray_3.setOnClickListener {
            image.setImageBitmap(BitmapUtils.bitmapGray(bitmap!!,2))
        }
        button_inverse.setOnClickListener {
            image.setImageBitmap(BitmapUtils.brightness(bitmap!!,0.8))
        }
        button_save.setOnClickListener {
            image.setImageBitmap(BitmapUtils.flip(bitmap!!))
        }
    }
    external fun stringFromJni():String
}