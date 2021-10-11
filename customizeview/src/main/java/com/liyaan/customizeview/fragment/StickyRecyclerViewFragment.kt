package com.liyaan.customizeview.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cs.customize.bean.Provience
import com.liyaan.customizeview.R
import com.liyaan.customizeview.view.StickyRecyclerView
import kotlinx.android.synthetic.main.fragment_sticky_recyclerview.*

class StickyRecyclerViewFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sticky_recyclerview, container, false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val cityList = ArrayList<Provience>()

        cityList.add(Provience("北京", "110", "A"))
        cityList.add(Provience("北京", "110", "A"))
        cityList.add(Provience("北京", "110", "B"))
        cityList.add(Provience("北京", "110", "B"))
        cityList.add(Provience("北京", "110", "C"))
        cityList.add(Provience("北京", "110", "C"))
        cityList.add(Provience("北京", "110", "C"))
        cityList.add(Provience("北京", "110", "D"))
        cityList.add(Provience("北京", "110", "E"))
        cityList.add(Provience("北京", "110", "E"))
        cityList.add(Provience("北京", "110", "E"))
        cityList.add(Provience("北京", "110", "E"))
        cityList.add(Provience("北京", "110", "F"))

        stickyRecyclerView.setAdapter(MyAdapter(requireContext(), cityList))
    }

    class MyAdapter(private val mContext: Context, val data: ArrayList<Provience>):StickyRecyclerView.StickyAdapter<MyAdapter.MyHolder>(){
        class MyHolder(item: View) : RecyclerView.ViewHolder(item) {
            val tvProvience: TextView = item.findViewById(R.id.tvProvience)
        }

        override fun getItemViewTitle(position: Int): String {
            return data[position].provienceIndex
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            val view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_sticky_recyclerview, parent, false)
            return MyHolder(view)
        }

        override fun getItemCount(): Int  = data.size

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.tvProvience.text = data[position].provienceName
        }
    }
}