package com.liyaan.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.mynew.R
import com.liyaan.utils.Bitmaps
import kotlinx.android.synthetic.main.activity_function.*

class FunctionActivity:AppCompatActivity() {
    lateinit var bitmap:Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_function)
        bitmap = BitmapFactory.decodeStream(assets.open("pic2.jpg"))
        ivPic.setImageBitmap(bitmap)

        btMirrorX.setOnClickListener {
            bitmap = Bitmaps.mirrorX(bitmap)
            ivPic.setImageBitmap(bitmap)
        }
        btMirrorY.setOnClickListener {
            bitmap = Bitmaps.mirrorY(bitmap)
            ivPic.setImageBitmap(bitmap)
        }

        btRoate.setOnClickListener {
            bitmap = Bitmaps.rotate(bitmap,90f)
            ivPic.setImageBitmap(bitmap)
        }
        btnScale.setOnClickListener {
            bitmap = Bitmaps.scale(bitmap,0.9f)
            ivPic.setImageBitmap(bitmap)
        }
        btnScaleToSize.setOnClickListener {
            bitmap = Bitmaps.scale(bitmap,720,1080)
            ivPic.setImageBitmap(bitmap)
        }
        btnCorp.setOnClickListener {
            bitmap = Bitmaps.crop(bitmap,500, 800)
            ivPic.setImageBitmap(bitmap)
        }
        btnCircle.setOnClickListener {
            bitmap = Bitmaps.cropCircle(bitmap,500)
            ivPic.setImageBitmap(bitmap)
        }
    }
}