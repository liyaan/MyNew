package com.liyaan.mvvm.net.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liyaan.mvvm.net.api.HttpManager
import com.liyaan.mvvm.net.api.NewsApi
import com.liyaan.mvvm.net.ui.bean.DataBean
import com.liyaan.mvvm.net.ui.bean.Result
import com.liyaan.mvvm.net.ui.bean.ResultBean
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataViewModel:ViewModel() {
    val liveData = MutableLiveData<MutableList<DataBean>>()
    val api: NewsApi = HttpManager.getRetrofit().create(NewsApi::class.java)

    fun getLiveData(type:String):MutableLiveData<MutableList<DataBean>>{
        api.getNews(type,"bc0a3a53be1e97115c2313e638662cae")
            .enqueue(object:Callback<Result<ResultBean<DataBean>>>{
                override fun onFailure(call: Call<Result<ResultBean<DataBean>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<Result<ResultBean<DataBean>>>,
                    response: Response<Result<ResultBean<DataBean>>>
                ) {
                    response?.let {
                        liveData.value = it.body()?.result?.data
                    }
                }

            })
        return liveData
    }
}