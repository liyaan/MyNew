package com.liyaan.mynew

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ChannelAdapter(context: Context,list:MutableList<String>,isUserChannel:Boolean): BaseAdapter() {
    private val mContext = context
    private val listData = list
    private var inFlater:LayoutInflater
    private val mIsUserChannel = isUserChannel

    var mAnimState = AnimState.IDLE

    var mReadyToRemove:Int = -1

    init {
        inFlater = LayoutInflater.from(context)
    }
    companion object{
        @JvmStatic
        var mInEditSate:Boolean = false
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var mConvertView=convertView
        var viewHolder:ViewOneHolder
        if (convertView==null){
            mConvertView = inFlater.inflate(R.layout.channel_item,parent,false)
            viewHolder = ViewOneHolder(mConvertView)
            mConvertView?.tag = viewHolder
        }else{
            viewHolder = mConvertView?.tag as ViewOneHolder
        }
        val tvItem = viewHolder.tv
        val ivEdit = viewHolder.iv

        if (mInEditSate){
            ivEdit.visibility = View.VISIBLE
            ivEdit.setBackgroundResource(if (!mIsUserChannel)R.drawable.ic_baseline_add_24 else R.drawable.ic_baseline_remove_24)
        }else{
            ivEdit.visibility = View.GONE
        }
        if (mReadyToRemove==position || mAnimState == AnimState.TRANSLATION && position==count-1){
            tvItem.text = ""
            tvItem.isSelected = true
            ivEdit.visibility = View.INVISIBLE
        }else{
            tvItem.text = listData[position]
            tvItem.isSelected = false
        }
//        ivEdit.visibility = View.GONE
        return mConvertView
    }

    override fun getItem(position: Int): Any? {
        return listData.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listData.size
    }

    fun add(channelName:String){
        listData.add(channelName)
        notifyDataSetChanged()
    }
    fun remove(index:Int){
        if(index>=0 && index<listData.size){
            listData.removeAt(index)
        }
        notifyDataSetChanged()
    }
    fun remove(){
        remove(mReadyToRemove)
        mReadyToRemove = -1
    }
    fun setRemove(index:Int):String {
        mReadyToRemove = index
        notifyDataSetChanged()
        return listData[index]
    }

    fun setTranslating(tranSlating:Boolean){
        mAnimState = if (tranSlating) AnimState.TRANSLATION else AnimState.IDLE
    }
    class ViewOneHolder(view:View?){
        var tv:TextView
        var iv:ImageView
        init {
            tv = view!!.findViewById(R.id.tv_item)
            iv = view!!.findViewById(R.id.iv_icon)
        }
    }
    enum class AnimState{
        IDLE,TRANSLATION
    }
}