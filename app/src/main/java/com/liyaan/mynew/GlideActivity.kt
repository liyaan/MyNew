package com.liyaan.mynew

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.glide.KGlide


class GlideActivity:AppCompatActivity() {
    val image_url ="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F004%2F531%2F381%2F4339f96900344574a0c8ca272a7b8f27.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1626246589&t=9916852dd6cf6a134702ee0f4eab23d1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_img)
        KGlide.with(this).load(image_url).into(findViewById(R.id.imageView1))
//        KGlide.with(this).load(image_url).into(findViewById(R.id.imageView2))
//        KGlide.with(this).load(image_url).into(findViewById(R.id.imageView2))
//        KGlide.with(this).load(image_url).into(findViewById(R.id.imageView2))
//        KGlide.with(this).load(image_url).into(findViewById(R.id.imageView2))
//        KGlide.with(this).load(image_url).into(findViewById(R.id.imageView2))
    }
}