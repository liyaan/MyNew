package com.liyaan.mynew

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.get
import com.liyaan.annotation.BindView
import com.liyaan.annotation.OnClick
import com.liyaan.api.ButterKnife
import com.liyaan.proxy.ProxyClass
import com.liyaan.proxy.WeatherApi
import com.liyaan.proxylibrary.LogProxy
import com.liyaan.retrofitlibrary.ZyangRetrofit
import com.liyaan.utils.ImageManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


const val data:String = "data.json"
const val ANIM_DURATION:Long = 300
class MainActivity : AppCompatActivity(),AdapterView.OnItemClickListener {
    private val mUserList = ArrayList<String>()
    private val mOtherList = ArrayList<String>()
    private var mUserAdapter:ChannelAdapter? = null
    private var mOtherAdapter:ChannelAdapter? = null
    @BindView(R.id.main_title)
    lateinit var main_title:AppCompatTextView
    @BindView(R.id.main_title_one)
    lateinit var main_title_one:AppCompatTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
//        val zyangRetrofit: ZyangRetrofit? = ZyangRetrofit.Builder()
//            .baseUrl(url)
//            ?.build()
//        val weatherApi = zyangRetrofit!!.create(WeatherApi::class.java)
//        val call = weatherApi.getWeather("18633002164")
//        call?.enqueue(object:Callback{
//            override fun onFailure(call: Call, e: IOException) {
//                Log.i("aaaaa", "onResponse: onFailure"+e.message);
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                Log.i("aaaaa", "onResponse: get: "+response.body()?.string());
//                response.close();
//            }
//
//        })
        initData()
        LogProxy().log(ProxyClass())
        main_title.text = "aaaaaaaaaaaaaa"
        main_title_one.text = "bbbbbbbbbbbbbbbb"
        ImageManager.with(this)
            .load("https://lupic.cdn.bcebos.com/20210629/2000029256_14.jpg")
            .placeholder(R.mipmap.ic_launcher)?.into(main_imgview)
        mUserAdapter = ChannelAdapter(this,mUserList,true)
        mOtherAdapter = ChannelAdapter(this,mOtherList,false)
        user_gridview.adapter = mUserAdapter
        other_gridview.adapter = mOtherAdapter
        user_gridview.onItemClickListener = this
        other_gridview.onItemClickListener = this
        open_select.setOnCheckedChangeListener { _, isChecked ->
            ChannelAdapter.mInEditSate = isChecked
            if (ChannelAdapter.mInEditSate){
                tv_more.visibility = View.VISIBLE
                other_gridview.visibility = View.VISIBLE
            }else{
                tv_more.visibility = View.GONE
                other_gridview.visibility = View.GONE
            }
            mUserAdapter?.notifyDataSetChanged()
            mOtherAdapter?.notifyDataSetChanged()
        }
    }

    private fun initData(){
        val input = assets.open(data)
        val length = input.available()
        val buffer = ByteArray(length)
        input.read(buffer)
        val dataStr = String(buffer,Charsets.UTF_8)
        val jsonObject = JSONObject(dataStr)
        val userArray = jsonObject.optJSONArray("user")
        val otherArray = jsonObject.optJSONArray("other")
        for (i in 0 until userArray.length()){
            mUserList.add(userArray.optString(i))
        }
        for (i in 0 until otherArray.length()){
            mOtherList.add(otherArray.optString(i))
        }
        Log.i("aaa",mUserList.toString())
        Log.i("aaa",mOtherList.toString())
    }

    private fun moveAnimation(moveView:View,startPos:IntArray,endPos:IntArray,duration:Long){
        val animation=TranslateAnimation(startPos[0].toFloat(),endPos[0].toFloat(),
            startPos[1].toFloat(),endPos[1].toFloat())
        animation.duration = duration
        animation.fillAfter = false
        animation.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                (moveView.parent as ViewGroup).removeView(moveView)
                reauestAdapter()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        moveView.startAnimation(animation)
    }

    private fun reauestAdapter() {
        mUserAdapter?.setTranslating(false)
        mOtherAdapter?.setTranslating(false)

        mUserAdapter?.remove()
        mOtherAdapter?.remove()
    }

    private fun getCloneView(view:View):ImageView{
        //旧版本
//        view.destroyDrawingCache()
//        view.isDrawingCacheEnabled = true
//        val cache = Bitmap.createBitmap(view.drawingCache)
//        view.isDrawingCacheEnabled = false
//        val imageView = ImageView(this)
//        imageView.setImageBitmap(cache)
        val bitmap = Bitmap.createBitmap(view.width,view.height,Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        return imageView
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (ChannelAdapter.mInEditSate){
            var currentView:GridView
            var anotherView:GridView
            if (parent==user_gridview){
                currentView = user_gridview
                anotherView = other_gridview
            }else{
                currentView = other_gridview
                anotherView = user_gridview
            }
            val startPos = IntArray(2)
            val endPos = IntArray(2)
            view?.getLocationInWindow(startPos)

            val currentAdapter = currentView.adapter as ChannelAdapter
            val anotherAdapter = anotherView.adapter as ChannelAdapter
            anotherAdapter.setTranslating(true)
            anotherAdapter.add(currentAdapter.setRemove(position))
            val cloceView = getCloneView(view!!)
            (window.decorView as ViewGroup).addView(cloceView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            currentView.post {
                val lastView = anotherView.get(anotherView.childCount-1)
                lastView.getLocationInWindow(endPos)
                moveAnimation(cloceView,startPos,endPos,ANIM_DURATION)
            }

        }else {
            Toast.makeText(this@MainActivity, mUserList.get(position), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.main_title,R.id.main_title_one)
    fun click2(view: View) {
        when (view.id) {
            R.id.main_title -> Toast.makeText(this, "test2", Toast.LENGTH_SHORT).show()
            R.id.main_title_one->Toast.makeText(this, "test3", Toast.LENGTH_SHORT).show()
        }
    }
}