package com.liyaan.mvvm.net.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter<T>:RecyclerView.Adapter<BaseHolder> {
    private var datas: MutableList<T>? = null
    private var layoutIds: IntArray? = null
    constructor(datas:MutableList<T>,layoutId:Int){
        this.datas = datas
        this.layoutIds = IntArray(layoutId)
    }
    constructor(datas:MutableList<T>,layoutIds:IntArray){
        this.datas = datas
        this.layoutIds = layoutIds
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return BaseHolder.getHolder(layoutIds!![viewType],parent)
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        onBinDatas(holder, datas!![position],position)
    }
    protected abstract fun onBinDatas(holder: BaseHolder?, t: T, position: Int)
    override fun getItemCount(): Int {
        return if (datas == null) 0 else datas!!.size
    }
}