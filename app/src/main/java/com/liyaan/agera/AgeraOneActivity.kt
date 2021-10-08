package com.liyaan.agera

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.agera.Repositories
import com.google.android.agera.Updatable
import com.liyaan.mynew.R
import kotlinx.android.synthetic.main.activity_agera_one_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class AgeraOneActivity:AppCompatActivity() {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    private val updatable = Updatable {
        val currentTime = mutableRepository.get()
        textView_date.text = currentTime
    }
    private val mutableRepository = Repositories.mutableRepository(getCurrentTime())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agera_one_layout)
        button_launch.setOnClickListener {
            testLaunch()
            job
        }
        button_agera.setOnClickListener {
            mutableRepository.accept(getCurrentTime())
        }
//        GlobalScope.launch {
//            val token = getToken()
//            val info = setToken(token)
//            setUserInfo(info)
//        }
//        repeat(8){
//            Log.e("aaaa","thread$it")
//        }
//        GlobalScope.launch {
//            val result1 = async {
//                getResult1()
//            }
//            val result2 = async {
//                getResult2()
//            }
//            val result = result1.await()+result2.await()
//            Log.e("aaaa","result = $result")
//        }
    }
    private fun testLaunch() = runBlocking {
        repeat(8) {
            Log.i("aaaaa","Aaaaaaaa${System.currentTimeMillis()}")
            delay(1000)
        }
    }
    private fun setUserInfo(userInfo: String) {
        Log.e("aaaaaa", userInfo)
    }
    private val job = GlobalScope.launch {
        delay(6000)
        Log.i("aaaaa","Aaaaaaaa${Thread.currentThread().id}")
    }

    private suspend fun getToken():String{
        return "token${System.currentTimeMillis()}"
    }

    private suspend fun setToken(token:String):String{
        return "$token   WWWWWWWW"
    }

    private suspend fun getResult1():Int{
        return 1
    }
    private suspend fun getResult2():Int{
        return 2
    }

    private fun getCurrentTime():String{
        return format.format(Date())
    }

    override fun onResume() {
        super.onResume()
        mutableRepository.addUpdatable(updatable)
    }

    override fun onPause() {
        super.onPause()
        mutableRepository.removeUpdatable(updatable)
    }
}