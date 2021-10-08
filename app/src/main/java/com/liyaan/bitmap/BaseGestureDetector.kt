package com.liyaan.bitmap

import android.content.Context
import android.view.MotionEvent

abstract class BaseGestureDetector(private val mContext: Context) {
    protected var mGestureInProcess = false
    protected var mPreMotionEvent:MotionEvent? = null
    protected var mCurrentMotionEvent:MotionEvent? = null

    fun onTouchEvent(event:MotionEvent):Boolean{
        if (!mGestureInProcess){
            handleStartProgressEvent(event)
        }else{
            handleInProgressEvent(event)
        }
        return true
    }

    protected fun resetState(){
        if (mPreMotionEvent != null) {
            mPreMotionEvent?.recycle()
            mPreMotionEvent = null
        }

        if (mCurrentMotionEvent != null) {
            mCurrentMotionEvent?.recycle()
            mCurrentMotionEvent = null
        }
        mGestureInProcess = false
    }

    abstract fun handleInProgressEvent(event: MotionEvent)

    abstract fun handleStartProgressEvent(event: MotionEvent)

    abstract fun updateStateByEvent(event: MotionEvent)
}