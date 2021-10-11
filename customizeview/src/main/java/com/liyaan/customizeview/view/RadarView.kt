package com.liyaan.customizeview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class RadarView:View {
    private lateinit var mPaint:Paint
    private var mCenterX = 0
    private var mCenterY = 0
    private var mWidth = 0
    private var mHright = 0
    private lateinit var mTitlePaint:Paint
    private lateinit var mValuePaint:Paint

    private var mCount = 6
    private var mRdius:Float = 0f
    private var mAngle:Float = 0f

    var mTitles = ArrayList<String>()
    var mData = ArrayList<Double>()

    var mMaxValue:Float = 100f


    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 2f
        mPaint.isAntiAlias = true

        mTitlePaint = Paint()
        mTitlePaint.style = Paint.Style.FILL
        mTitlePaint.color = Color.BLACK
        mTitlePaint.strokeWidth = 2f
        mTitlePaint.isAntiAlias = true
        mTitlePaint.textAlign = Paint.Align.CENTER
        mTitlePaint.textSize = 40f

        mValuePaint = Paint()
        mValuePaint.color = Color.RED
        mValuePaint.isAntiAlias = true
        mTitlePaint.strokeWidth = 10f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHright = h
        mCenterX = w/2
        mCenterY = h/2
        mRdius = Math.min(w,h)/2*0.7f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPoint(mCenterX.toFloat(), mCenterY.toFloat(), mPaint)
        drawPolygon(canvas)
        drawLines(canvas)
        drawTitle(canvas)
        drawRegion(canvas)
    }

    private fun drawPolygon(canvas: Canvas) {
        val path = Path()
        mAngle = (2*Math.PI/mCount).toFloat()
        val r = mRdius/(mCount - 1)
        for (i in 0 until mCount){
            val curR = r*i
            path.reset()
            for (j in 0 until mCount){
                if (j==0){
                    path.moveTo(mCenterX+curR, mCenterY.toFloat())
                }else{
                    val x = (mCenterX+curR*Math.cos((mAngle*j).toDouble())).toFloat()
                    val y =
                        (mCenterY + curR * Math.sin(mAngle * j.toDouble())).toFloat()
                    path.lineTo(x,y)
                }
            }
            path.close()
            canvas.drawPath(path,mPaint)
        }
    }
    private fun drawLines(canvas:Canvas) {
        val path = Path()
        for (i in 0 until mCount){
            path.reset()
            path.moveTo(mCenterX.toFloat(), mCenterY.toFloat())
            val x = (mCenterX+mRdius*Math.cos((mAngle*i).toDouble()))
            val y = (mCenterY + mRdius * Math.sin(mAngle * i.toDouble()))
            path.lineTo(x.toFloat(), y.toFloat())
            canvas.drawPath(path,mPaint)
        }
    }
    private fun drawTitle(canvas:Canvas) {
        if (mTitles.size == 0) return
        val fontMetrics = mTitlePaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent
        val textRadius = mRdius+fontHeight
        val pi = Math.PI
        for(i in 0 until mCount){
            val degrees = mAngle*i
            val x = mCenterX+textRadius*Math.cos(degrees.toDouble())
            val y = mCenterY+textRadius*Math.sin(degrees.toDouble())
            val dis = mTitlePaint.measureText(mTitles[i])/(mTitles[i].length)
            if (degrees>=0 && degrees<pi/2){
                canvas.drawText(mTitles[i], (x+dis).toFloat(), y.toFloat(),mTitlePaint)
            }else if (degrees>=(pi/2) && degrees<pi){
                canvas.drawText(mTitles[i], (x-dis).toFloat(), y.toFloat(),mTitlePaint)
            }else if (degrees>=pi && degrees < 3 * pi / 2){
                canvas.drawText(mTitles[i], (x - dis).toFloat(), y.toFloat(), mTitlePaint)
            } else if (degrees >= 3 * pi / 2 && degrees <= 2 * pi) {
                canvas.drawText(mTitles[i], x.toFloat(), y.toFloat(), mTitlePaint)
            }
        }
    }
    private fun drawRegion(canvas:Canvas) {
        if (mData.size == 0) return
        mValuePaint.alpha = 255
        val path = Path()
        for(i in 0 until mCount){
            val perCentr = mData.get(i)/mMaxValue
            val perRadius = perCentr*mRdius
            val x =mCenterX + perRadius * Math.cos(mAngle * i.toDouble())
            val y =mCenterY + perRadius * Math.sin(mAngle * i.toDouble())
            if (i==0){
                path.moveTo(x.toFloat(), y.toFloat())
            }else{
                path.lineTo(x.toFloat(), y.toFloat())
            }
            //绘制小圆点
            canvas.drawCircle(x.toFloat(), y.toFloat(), 10f, mValuePaint)
        }
        path.close()
        mValuePaint.style = Paint.Style.STROKE
        canvas.drawPath(path,mValuePaint)
        mValuePaint.alpha = 128
        mValuePaint.style = Paint.Style.FILL
        canvas.drawPath(path, mValuePaint)
    }
    fun setTitles(mTitles: ArrayList<String>) {
        this.mTitles = mTitles
        invalidate()
    }


    fun setData(data: ArrayList<Double>) {
        mData = data
        invalidate()
    }

    fun setMaxValue(maxValue: Float) {
        mMaxValue = mMaxValue
        invalidate()
    }
}