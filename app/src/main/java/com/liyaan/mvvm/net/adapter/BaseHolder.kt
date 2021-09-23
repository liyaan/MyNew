package com.liyaan.mvvm.net.adapter

import android.graphics.Bitmap
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liyaan.App
import com.liyaan.mvvm.net.utils.GlideApp


class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    private val views: SparseArray<View> = SparseArray()
    private val itemOneView = itemView

    fun getItemView(): View {
        return itemOneView
    }

    fun getViewId(viewId: Int): View {
        var view = views[viewId]
        if (view == null) {
            view = itemOneView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view
    }

    companion object{
        fun <T : BaseHolder> getHolder(layoutId: Int, parant: ViewGroup): T {
            return BaseHolder(
                LayoutInflater.from(App.getContext())
                    .inflate(layoutId, parant, false)
            ) as T
        }
    }

    fun setOnClickListener(
        viewId: Int,
        onClickListener: View.OnClickListener?
    ): BaseHolder? {
        getViewId(viewId).setOnClickListener(onClickListener)
        return this
    }

    fun setText(viewId: Int, msg: String?): BaseHolder? {
        (getViewId(viewId) as TextView).text = msg
        return this
    }

    fun setText(viewId: Int, resId: Int): BaseHolder? {
        (getViewId(viewId) as TextView).setText(resId)
        return this
    }

    fun setImageView(ImageViewId: Int, url: String?): BaseHolder? {
        GlideApp.with(App.getContext()).load(url)
            .into(getViewId(ImageViewId) as ImageView)
        return this
    }

    fun setImageView(imagViewId: Int, resId: Int): BaseHolder? {
        (getViewId(imagViewId) as ImageView).setImageResource(resId)
        return this
    }


    fun setImageView(imagViewId: Int, bitmap: Bitmap?): BaseHolder? {
        (getViewId(imagViewId) as ImageView).setImageBitmap(bitmap)
        return this
    }
}