package com.liyaan.mynew


import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.liyaan.utils.DensityUtil
import com.yl.recyclerview.wrapper.DragAndDropWrapper
import com.yl.recyclerview.wrapper.HeaderAndFooterWrapper
import com.yl.recyclerview.wrapper.LoadMoreWrapper
import kotlinx.android.synthetic.main.activity_recycleview.*


class RecyclerViewActivity:AppCompatActivity() {
    private var dataList = ArrayList<String>()
    private var mLoadMoreAdapter:LoadMoreAdapter? = null
    private var loadMoreWrapper:LoadMoreWrapper? =null
    private var mHeaderAndFooterWrapper:HeaderAndFooterWrapper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycleview)
        initData()
        initAdapter()
    }
    private fun initAdapter(){
        //因为androidx与Android不兼容
        mLoadMoreAdapter = LoadMoreAdapter(dataList)
//        mHeaderAndFooterWrapper = HeaderAndFooterWrapper(mLoadMoreAdapter);
//        loadMoreWrapper = LoadMoreWrapper(mLoadMoreAdapter)
//        customLoadingView()
//        addHeaderAndFooterView()
        val mDragAndDropWrapper = DragAndDropWrapper(mLoadMoreAdapter, dataList, 200)
        mDragAndDropWrapper.attachToRecyclerView(mRecyclerView, true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mDragAndDropWrapper
//        mRecyclerView.addOnScrollListener(object:OnScrollListener(){
//            override fun onLoadMore() {
//                loadMoreWrapper?.setLoadStateNotify(loadMoreWrapper!!.LOADING)
//                if (dataList.size<15){
//                    // 获取数据
//                    // 设置数据加载完成状态，可自定义布局
//                    loadMoreWrapper?.setLoadStateNotify(loadMoreWrapper!!.LOADING_COMPLETE);
//                }else{
//// 设置所有数据加载完成状态，可自定义布局
//                    loadMoreWrapper?.setLoadStateNotify(loadMoreWrapper!!.LOADING_END);
//                }
//            }
//
//        })
        // 刷新数据需要使用外层Adapter
//        loadMoreWrapper?.notifyDataSetChanged();
    }


    private fun customLoadingView(){
        val progressBar = ProgressBar(this)
        loadMoreWrapper?.setLoadingView(progressBar)
        val textView = TextView(this)
        textView.setText("End")
        loadMoreWrapper?.setLoadingEndView(textView)

        loadMoreWrapper?.setLoadingViewHeight(DensityUtil.dp2px(this,50f))
    }

    private fun addHeaderAndFooterView() {
        // Add header view
        val headerView: View = View.inflate(this, R.layout.layout_header_footer, null)
        val headerItem: TextView = headerView.findViewById(R.id.tv_item)
        headerItem.text = "HeaderView"
        mHeaderAndFooterWrapper?.addHeaderView(headerView)

        // Add footer view
        val footerView: View = View.inflate(this, R.layout.layout_header_footer, null)
        val footerItem: TextView = footerView.findViewById(R.id.tv_item)
        footerItem.text = "FooterView"
        mHeaderAndFooterWrapper?.addFooterView(footerView)
    }
    private fun initData(){
        var latter:Char = 'A'
        for (i in 0..25){
            dataList.add(latter.toString())
            latter++
        }
    }


}