package com.liyaan.customizeview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickyRecyclerView: RecyclerView {

    var mLineHeight = 0f
    var mTitleHeight = 0f
    var mLeftMargin = 0f
    var mTextSize = 0f


    val dividerColor = "#E1E1E1"
    val titleColor = "#666666"
    val titleBg = "#F2F2F2"

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init(context)
    }

    private fun init(context: Context) {
        layoutManager = LinearLayoutManager(context, VERTICAL,false)
        mLineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            0.5f, context.resources.displayMetrics)
        mTitleHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30f,
            context.resources.displayMetrics)
        mLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            10f, context.resources.displayMetrics)
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            20f, context.resources.displayMetrics)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
    }
    // 让 adapter 必须继承 StickyAdapter
    fun setAdapter(adapter: StickyAdapter<*>) {
        addItemDecoration(StickyItemDecoration(adapter))
        super.setAdapter(adapter)
    }
    inner class StickyItemDecoration(@NonNull private val mAdapter: StickyAdapter<*>)
        : ItemDecoration() {
        private val mPaint = Paint()
        init {
            mPaint.style = Paint.Style.FILL
            mPaint.textSize = mTextSize
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            super.getItemOffsets(outRect, view, parent, state)
            val pos = parent.getChildAdapterPosition(view)
            if (mAdapter.needTitle(pos))
                outRect.top = mTitleHeight.toInt()
            else
                outRect.top = mLineHeight.toInt()
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: State) {
            super.onDraw(c, parent, state)
            val left = parent.left
            val right = parent.measuredWidth - parent.paddingRight
            for(i in 0 until childCount){
                val child = parent.getChildAt(i)
                val param = child.layoutParams as RecyclerView.LayoutParams
                val pos = parent.getChildAdapterPosition(child)
                if (mAdapter.needTitle(pos)){
                    val top = child.top+param.topMargin-mTitleHeight
                    val bottom = top + mTitleHeight
                    mPaint.color = Color.parseColor(titleBg)
                    c.drawRect(left.toFloat(), top, right.toFloat(),
                        bottom - mLineHeight, mPaint)
                    mPaint.color = Color.parseColor(dividerColor)
                    c.drawRect(left.toFloat(), bottom - mLineHeight,
                        right.toFloat(), bottom, mPaint)
                    mPaint.color = Color.parseColor(titleColor)
                    val title = mAdapter.getItemViewTitle(pos)
                    val titleRect = Rect(0, 0, 0, 0)
                    mPaint.getTextBounds(title, 0, title.length, titleRect)
                    c.drawText(mAdapter.getItemViewTitle(pos), left + mLeftMargin,
                        (top + mTitleHeight * 0.5 + titleRect.height() * 0.5).toFloat(), mPaint)
                }else {
                    val top = child.top + param.topMargin - mLineHeight
                    val bottom = top + mLineHeight
                    mPaint.color = Color.parseColor(dividerColor)
                    c.drawRect(left.toFloat() + mLeftMargin, top, right.toFloat(), bottom, mPaint)
                }
            }

        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: State) {
            super.onDrawOver(c, parent, state)
            val left = parent.left
            val right = parent.measuredWidth - parent.paddingRight
            val top = parent.top - parent.paddingTop
            val bottom = top + mTitleHeight
            mPaint.color = Color.parseColor(titleBg)
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, mPaint)

            val pos = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            mPaint.color = Color.parseColor(titleColor)
            val title = mAdapter.getItemViewTitle(pos)
            val titleRect = Rect(0, 0, 0, 0)
            mPaint.getTextBounds(title, 0, title.length, titleRect)
            c.drawText(mAdapter.getItemViewTitle(pos), left + mLeftMargin,
                (top + mTitleHeight * 0.5 + titleRect.height() * 0.5).toFloat(), mPaint)
        }
    }

    abstract class StickyAdapter<T:ViewHolder>:RecyclerView.Adapter<T>(){
        abstract fun getItemViewTitle(position:Int):String
        fun needTitle(position: Int):Boolean{
            return position>-1 && (position==0 ||
                    getItemViewTitle(position)!=getItemViewTitle(position-1))
        }
    }
}