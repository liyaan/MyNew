package com.liyaan.onedownload

import org.json.JSONObject
import java.io.Serializable

class DownloadBean:Serializable {
    var title:String? = null
    var content:String? = null
    var url:String? = null
    var md5:String? = null
    var versionCode:String? = null

    companion object{
        @JvmStatic
        fun parse(jsonObject: JSONObject):DownloadBean{
            val downloadBean: DownloadBean = DownloadBean()
            downloadBean.title = jsonObject.getString("title")
            downloadBean.content = jsonObject.getString("content")
            downloadBean.url = jsonObject.getString("url")
            downloadBean.md5 = jsonObject.getString("md5")
            downloadBean.versionCode = jsonObject.getString("versionCode")
            return downloadBean
        }
    }
}