package com.liyaan.bitmap

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent

class MoveGestureDetector(mContext: Context, var mListener: OnMoveGestureListener) : BaseGestureDetector(mContext) {
    private lateinit var mCurrentPointer:PointF
    private lateinit var mPrePointer:PointF

    private val mDeltaPointer = PointF()

    private val mExtenalPointer = PointF()

    override fun handleInProgressEvent(event: MotionEvent) {
        when(event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_UP->{
                mListener.onMoveEnd(this)
                resetState()
            }
            MotionEvent.ACTION_MOVE->{
                updateStateByEvent(event)
                val update = mListener.onMove(this)
                if (update){
                    mPreMotionEvent?.recycle()
                    mPreMotionEvent = MotionEvent.obtain(event)
                }
            }
        }
    }

    override fun handleStartProgressEvent(event: MotionEvent) {
        when(event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN->{
                resetState()
                mPreMotionEvent = MotionEvent.obtain(event)
                updateStateByEvent(event)
            }
            MotionEvent.ACTION_MOVE->{
                mGestureInProcess = mListener.onMoveBegin(this)
            }
        }
    }

    override fun updateStateByEvent(event: MotionEvent) {
        val prev = mPreMotionEvent
        mPrePointer = calculateFocalPointer(prev!!)
        mCurrentPointer = calculateFocalPointer(event)
        val skipThisMoveEvent = prev.pointerCount!= event.pointerCount
        mExtenalPointer.x = if (skipThisMoveEvent) 0f else mCurrentPointer.x - mPrePointer.x
        mExtenalPointer.y = if (skipThisMoveEvent) 0f else mCurrentPointer.y - mPrePointer.y
    }
    private fun calculateFocalPointer(event: MotionEvent): PointF {
        val pointerCount = event.pointerCount
        var x = 0f
        var y = 0f
        for (i in 0 until pointerCount){
            x+=event.getX(i)
            y+=event.getY(i)
        }
        x/=pointerCount
        y/=pointerCount
        return PointF(x,y)
    }
    fun getMoveX() = mExtenalPointer.x
    fun getMoveY() = mExtenalPointer.y
    interface OnMoveGestureListener{
        fun onMoveBegin(detector:MoveGestureDetector):Boolean
        fun onMove(detector: MoveGestureDetector):Boolean
        fun onMoveEnd(detector: MoveGestureDetector)
    }

    open class SimpleMoveGestureDetector:OnMoveGestureListener{
        override fun onMoveBegin(detector: MoveGestureDetector): Boolean {
            return true
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {

        }

    }
}