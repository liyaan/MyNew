package com.liyaan.recyclerview.http

import com.alibaba.fastjson.JSONObject
import com.liyaan.recyclerview.HttpUtils
import retrofit2.Call

/**
 * @Author : ChenSen
 * @Date : 2019/9/23 14:31
 *
 * @Desc :
 */
class PicListRepository private constructor() {

    companion object {
        private var instance: PicListRepository? = null

        fun getInstance(): PicListRepository {
            return instance ?: synchronized(this) {
                instance ?: PicListRepository().apply {
                    instance = this
                }
            }
        }
    }


    fun getMeizhi(page: Int): Call<JSONObject> {
        return HttpUtils.createService(IApi::class.java).getMeizhi(200, page)
    }

}