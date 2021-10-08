package com.liyaan.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liyaan.mynew.R
import kotlinx.android.synthetic.main.item_recyclerview.view.*

class DragAdapter(private val context: Context, private val mList: ArrayList<String>) : RecyclerView.Adapter<DragAdapter.ItemHolder>() {


    class ItemHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item2_recyclerview, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.itemView.tvText.text = mList[position]
    }

    fun delete(position: Int) {
        if (position < 0 || position > mList.size)
            return

        mList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun move(oldPosition: Int, targetPosition: Int) {
        val oldString = mList.removeAt(oldPosition)
        mList.add(if (targetPosition > oldPosition) targetPosition - 1 else targetPosition, oldString)
        notifyItemMoved(oldPosition, targetPosition)
    }
}