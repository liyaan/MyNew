package com.liyaan.mvvm.net.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.liyaan.App
import com.liyaan.mvvm.net.adapter.BaseHolder
import com.liyaan.mvvm.net.adapter.MutiLayoutAdapter
import com.liyaan.mvvm.net.model.DataViewModel
import com.liyaan.mvvm.net.ui.bean.DataBean
import com.liyaan.mynew.R


class NewsFragment():BaseFragment() {
    var position = 0

    @BindView(R.id.recycler)
    lateinit var mRecycler: RecyclerView
    var viewModel: DataViewModel? = null
    var dataBeansList: MutableList<DataBean> = ArrayList()
    private var Type =
        arrayOf("top", "shehui", "guonei", "guoji", "yule")
    companion object {
        fun newInstance(position:Int): NewsFragment {
            val args = Bundle()
            val fragment = NewsFragment()
            args.putInt("position", position);
            fragment.arguments = args
            Log.i("aaaaaaaaaa","xxxxx$position")
            return fragment
        }
    }
    override fun initData() {
        position = arguments?.getInt("position")!!

        viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        viewModel?.getLiveData(Type[position])?.observe(this,
            Observer<MutableList<DataBean>> { t ->
                t?.let {
                    t.forEach {
                        dataBeansList.add(it)
                    }
                    showData()
                }
            })
    }

    override fun initView(): Int {
        return R.layout.fragment_news;
    }

    private fun showData(){
        val adapter = object:MutiLayoutAdapter<DataBean>(dataBeansList,
            intArrayOf(R.layout.item_layout,R.layout.item)){
            override fun getItemType(position: Int): Int {
                if (dataBeansList[position].thumbnail_pic_s02!=null
                    && dataBeansList[position].thumbnail_pic_s03!=null){
                    return 0
                }else if (dataBeansList[position].thumbnail_pic_s!=null){
                    return 1
                }
                return 0
            }

            override fun onBind(holder: BaseHolder?,dataBean: DataBean, position: Int, itemType: Int) {
                if (itemType==0){
                    holder?.apply {
                        setText(R.id.title, dataBean.title)
                        setImageView(R.id.image_left, dataBean.thumbnail_pic_s)
                        setImageView(R.id.image_center, dataBean.thumbnail_pic_s02)
                        setImageView(R.id.image_right, dataBean.thumbnail_pic_s03)
                        setText(R.id.tv_time, dataBean.date)
                        setText(R.id.tv_type, dataBean.category)
                    }
                }else if (itemType==1){
                    holder?.apply {
                        setText(R.id.tv_title, dataBean.title)
                        setText(R.id.tv_time, dataBean.date)
                        setText(R.id.tv_type, dataBean.category)
                        setImageView(R.id.image, dataBean.thumbnail_pic_s)
                    }
                }

                holder?.getItemView()?.setOnClickListener {

                }
            }

        }
        mRecycler.layoutManager = LinearLayoutManager(App.getContext(),LinearLayoutManager.VERTICAL,false)
        mRecycler.adapter = adapter
    }
}