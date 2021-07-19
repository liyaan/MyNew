package com.liyaan.onedownload

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.mynew.R
import com.liyaan.okhttp.*
import kotlinx.android.synthetic.main.activity_demo_down_apk.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class DownDenoActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_down_apk)
        buttonDown.setOnClickListener {
//            AppUpdater.getInstance().getNetManager().get(Constants.APP_UPDATE_URL,object:NetCallBack{
//                override fun success(response: String?) {
//                    try {
//                        val jsonObject = JSONObject(response)
//                        val downBean = DownloadBean.parse(jsonObject)
//                        val versionCode: Long = downBean.versionCode!!.toLong()
//                        if (downBean.url!=null){
//                            if (versionCode > AppUtils.getVersionCode(this@DownDenoActivity)) {
//                                UpdateVersionShowDialog.show(this@DownDenoActivity, downBean);
//                                return;
//                            }
//                        }
//                    }catch (e:Exception){
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun failed(throwable: Throwable?) {
//
//                }
//
//            })
            val okhttp = OkHttpClient()
//            val requestBody = RequestBody().type(RequestBody.FORM)
//                .addParam("page","1").addParam("page_size","20")
            val jsonObject = JSONObject()
            jsonObject.put("page",1)
            jsonObject.put("page_size",20)
            val request = Request.Builder()
                .jsonString(jsonObject)
                .headers(getHeaderParams())
                .url("http://vipapp.tun.aitifen.cn/index/banner/getBannerList").builder()
            val call = okhttp.newCall(request)
            call?.enqueue(object:Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.i("TAG", e.message.toString())
                    show(e.toString())
                }
                override fun onResponse(call: Call, response: Response) {
                    val string = response.string()
                    Log.i("TAG", string!!)
                    show(string)
                }
            })
        }
    }
    private fun show(msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                this,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun getHeaderParams(): HashMap<String, String>? {
        val map =
            HashMap<String, String>()
//        map["auth-token"] = "MTU5Mjg1MDg3NDcwNw11.26=="
//        map["boarding-pass"] = ""
        map["Content-Type"] = "application/json; charset=utf-8"
        //        map.put("boarding-pass", "8F66ADEAF01C7BAE92685449D54EF9DF");
        return map
    }
}