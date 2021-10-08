package com.liyaan.recyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.cs.common.utils.toast
import com.liyaan.mynew.R
import com.liyaan.recyclerview.adapter.PicListAdapter
import com.liyaan.recyclerview.bean.Meizhi
import com.liyaan.recyclerview.decoration.MarginDecoration
import com.liyaan.recyclerview.http.PicListRepository
import kotlinx.android.synthetic.main.activity_loadpic.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PicListActivity:AppCompatActivity() {
    private val adapter by lazy {
        PicListAdapter(this)
    }
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loadpic)
        init()
        loadData(page)
    }

    private fun loadData(page:Int) {
        val call = PicListRepository.getInstance().getMeizhi(page)
        call.enqueue(object : Callback<JSONObject> {
            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                toast("获取数据失败！${t.message}")
            }

            override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    val jsonArray = jsonObject?.getJSONArray("results")
                    if (jsonArray.isNullOrEmpty()) {
                        toast("获取数据为空！}")
                    } else {
                        val list = jsonArray.toJavaList(Meizhi::class.java)
                        adapter.submitList(list)
                    }

                }else {
                    toast("获取数据失败！${response.errorBody()?.string()}")
                }
            }

        })
    }

    private fun init() {
        rvList.adapter = adapter
        val gridLayoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        rvList.layoutManager = gridLayoutManager
        rvList.addItemDecoration(MarginDecoration(10,10,10,10))

        rvList.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            //用来标记是否正在向上滑动
            var isSlidingUp = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    val lastVisiblePosition = gridLayoutManager.findLastCompletelyVisibleItemPosition()
                    val itemCount = gridLayoutManager.itemCount
                    if (lastVisiblePosition == (itemCount-1) && isSlidingUp){
                        page++
//                        loadData(page)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isSlidingUp = dy>0
            }
        })
    }
}