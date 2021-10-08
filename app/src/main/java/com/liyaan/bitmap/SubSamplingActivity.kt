package com.liyaan.bitmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.liyaan.mynew.R
import kotlinx.android.synthetic.main.activity_subsampling.*

class SubSamplingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subsampling)

//        ivSubSamplingView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP) //设置缩放类型
//        ivSubSamplingView.setDoubleTapZoomScale(1f)
        ivSubSamplingView.setImage(ImageSource.asset("qmsht.jpg"))


        //设置预览图
        //注意，目标图的大小必须先声明
//        ivSubSamplingView.setImage(
//                ImageSource.asset("qmsht.jpg").dimensions(30000, 926),
//                ImageSource.asset("pic2.jgp"))

    }


}