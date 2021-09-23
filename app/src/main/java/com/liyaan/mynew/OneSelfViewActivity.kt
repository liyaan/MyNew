package com.liyaan.mynew

import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_oneself_view.*

class OneSelfViewActivity: AppCompatActivity() {
    private var timer:CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oneself_view)
        onselfLoadingView.anim.start()
        timer = object:CountDownTimer(15_000,100L){
            override fun onFinish() {
            }

            override fun onTick(millisUntilFinished: Long) {
                oneselfLunarPhaseView.mPhase = (millisUntilFinished / 100L).toInt()
            }
        }
        oneselfLunarPhaseView?.onePhase = 75
        timer?.start()
    }
    fun clickRestart(view: View) {
        timer?.start()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d("MainActivity", "onConfigurationChanged#${newConfig.orientation}")
        super.onConfigurationChanged(newConfig)
        oneselfLunarPhaseView?.mRotate =
            if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                90
            } else {
                0
            }
    }
}