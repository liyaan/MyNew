package com.liyaan.mvvm.net.adapter


abstract class MutiLayoutAdapter<T>(datas: MutableList<T>, layoutIds: IntArray) :
    BaseAdapter<T>(datas, layoutIds) {
    override fun getItemViewType(position: Int): Int {
        return getItemType(position)
    }
    protected abstract fun getItemType(position: Int): Int

    override fun onBinDatas(holder: BaseHolder?, t: T, position: Int) {
        onBind(holder,t,position,getItemType(position))
    }
    protected abstract fun onBind(
        holder: BaseHolder?,
        t: T,
        position: Int,
        itemType: Int
    )
}