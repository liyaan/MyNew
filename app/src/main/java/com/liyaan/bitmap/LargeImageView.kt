package com.liyaan.bitmap

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.BitmapCompat
import java.io.InputStream

class LargeImageView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    private lateinit var mDecoder:BitmapRegionDecoder
    private var mGestureDetector:MoveGestureDetector
    private val mOptions = BitmapFactory.Options()
    @Volatile
    private var mRect = Rect()
    private var mWidth = 0
    private var mHeight = 0
    init {
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565
        mGestureDetector = MoveGestureDetector(context,
            object:MoveGestureDetector.SimpleMoveGestureDetector(){
                override fun onMove(detector: MoveGestureDetector): Boolean {
                    val moveX = detector.getMoveX().toInt()
                    val moveY = detector.getMoveY().toInt()
                    if (mWidth>width){
                        mRect.offset(-moveX,0)
                        checkWidth()
                        invalidate()
                    }
                    if (mHeight>height){
                        mRect.offset(0,-moveY)
                        checkHeight()
                        invalidate()
                    }
                    return true
                }
        })
    }
    fun setInputStream(inputStream: InputStream){
        try{
            val tempOptions = BitmapFactory.Options()
            tempOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream,null,tempOptions)
            mWidth = tempOptions.outWidth
            mHeight = tempOptions.outHeight
            mDecoder = BitmapRegionDecoder.newInstance(inputStream,false)
            requestLayout()
            invalidate()
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            inputStream.close()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidth = measuredWidth
        val viewHeight = measuredHeight

        //默认显示图片的中间位置
        mRect.left = mWidth / 2 - viewWidth / 2
        mRect.top = mHeight / 2 - viewHeight / 2
        mRect.right = mWidth / 2 + viewWidth / 2
        mRect.bottom = mWidth / 2 + viewWidth / 2
    }

    override fun onDraw(canvas: Canvas?) {
        val bitmap = mDecoder.decodeRegion(mRect,mOptions)
        canvas?.drawBitmap(bitmap,0f,0f,null)
    }
    fun checkWidth(){
        if (mRect.right>mWidth){
            mRect.right = mWidth
            mRect.left = mWidth - width
        }
        if (mRect.left<0){
            mRect.left = 0
            mRect.right = width
        }
    }
    fun checkHeight(){
        if (mRect.bottom>mHeight){
            mRect.bottom = mHeight
            mRect.top = mHeight - height
        }
        if (mRect.top<0){
            mRect.top = 0
            mRect.bottom = height
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGestureDetector.onTouchEvent(event)
        return true
    }

}