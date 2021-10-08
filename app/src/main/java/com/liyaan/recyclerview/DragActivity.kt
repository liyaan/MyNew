package com.liyaan.recyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.liyaan.mynew.R
import com.liyaan.recyclerview.adapter.DragAdapter
import com.liyaan.recyclerview.adapter.DragCallBack
import kotlinx.android.synthetic.main.activity_drag.*

class DragActivity:AppCompatActivity() {
    private val adapter by lazy {
        DragAdapter(this, list)
    }

    private val list = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag)

        init()
    }

    private fun init() {
        for (i in 0 until 100){
            list.add("item$i")
        }
        rvList.adapter = adapter
        rvList.layoutManager = GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false)
        val itemTouchHelper = ItemTouchHelper(DragCallBack(adapter))
        itemTouchHelper.attachToRecyclerView(rvList)
    }
}