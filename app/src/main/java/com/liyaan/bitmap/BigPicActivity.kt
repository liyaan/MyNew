package com.liyaan.bitmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.mynew.R
import kotlinx.android.synthetic.main.activity_bigpic.*

class BigPicActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bigpic)

        val inputStream = assets.open("qmsht.jpg")
        ivLargeImageView.setInputStream(inputStream)


        //显示图片的某一部分
        // test()

    }

}