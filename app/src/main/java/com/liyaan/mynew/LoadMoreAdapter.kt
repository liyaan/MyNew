package com.liyaan.mynew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LoadMoreAdapter(dataList:MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mDataList = dataList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recyclerview,parent,false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder  = holder as RecyclerViewHolder
        holder.tvItetm.setText(mDataList[position])
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItetm:TextView = itemView.findViewById(R.id.tv_item)
    }
}