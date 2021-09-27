package com.liyaan.mynew

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.liyaan.download.DownloadListener
import com.liyaan.eventBus.EventBus
import com.liyaan.eventBus.Subscribe
import com.liyaan.eventBus.ThreadMode
import com.liyaan.intentService.IntentServiceActivity
import com.liyaan.mynew.DownloadService.DownloadBinder
import com.liyaan.utils.ApkUtils
import kotlinx.android.synthetic.main.activity_download.*
import java.io.File


class DownloadAidlActivity:AppCompatActivity(), View.OnClickListener {
    private var downloadBinder: DownAidlService? = null
    var mDilog:Dialog? = null
    var bar:ProgressBar? = null
    private var connected = false
    private val url =
        "http://download.aitifen.com/android/ykt-release.apk"
    private val handler = object:Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            showDialog()
            bar?.progress = msg.obj as Int
        }
    }
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            downloadBinder = DownAidlService.Stub.asInterface(service)
            connected = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            connected = false
        }
    }
    private val callback = object:DownloadAidlListener.Stub(){
        override fun onSuccess() {
            if (mDilog!=null && mDilog!!.isShowing){
                mDilog?.dismiss()
            }
            Log.i("aaaaaaaaa","bbbbbbbbbb")
            val filename = url.substring(url.lastIndexOf("/"))
            val directory: String =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .path
            val file = File(directory + filename)
            ApkUtils.getInstall(file,
                this@DownloadAidlActivity)
        }

        override fun onFail() {
            if (mDilog!=null && mDilog!!.isShowing){
                mDilog?.dismiss()
            }
        }

        override fun onProgress(progress: Int) {
            val message = Message.obtain()
            message.obj = progress
            message.what = 1
            handler.sendMessage(message)
        }

        override fun onCanceled() {
        }

        override fun onPaused() {
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        EventBus.getDefault().register(this)
        val startDownload: Button = findViewById(R.id.start_download)
        val pauseDownload: Button = findViewById(R.id.pause_download)
        val cancelDownload: Button = findViewById(R.id.cancel_download)
        val openWea: Button = findViewById(R.id.open_weacher)
        val clickBtn: Button = findViewById(R.id.click_btn_text)
        val intentServiceBtn: Button = findViewById(R.id.click_intent_service)
        startDownload.setOnClickListener(this)
        pauseDownload.setOnClickListener(this)
        cancelDownload.setOnClickListener(this)
        openWea.setOnClickListener(this)
        clickBtn.setOnClickListener(this)
        intentServiceBtn.setOnClickListener(this)
        bindService()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )

        }
    }

    override fun onClick(v: View) {
        if (downloadBinder == null) {
            return
        }
        when (v.id) {
            R.id.start_download -> {
                downloadBinder?.startDownload(url)
                downloadBinder?.setListener(callback)
            }
            R.id.pause_download -> downloadBinder!!.pauseDownload()
            R.id.cancel_download -> downloadBinder!!.cancelDownload()
            R.id.open_weacher->getWechatApi()
            R.id.click_btn_text->{
                val intent = Intent(this,TestActivity::class.java)
                startActivity(intent)
            }
            R.id.click_intent_service->{
                val intent = Intent(this,IntentServiceActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show()
                finish()
            }
            else -> {
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN,priority = 50,sticky = true)
    fun testOne(msg:String){
        Log.i("msg", "msgOne = $msg")
        click_btn_text.text = msg
    }
    @Subscribe(threadMode = ThreadMode.MAIN,priority = 100,sticky = true)
    fun testTwo(msg:String){
        Log.i("msg", "msgTwo= $msg")
        click_btn_text.text = msg
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        unbindService(connection)
    }

    private fun showDialog(){
        if (mDilog==null){
            mDilog = Dialog(this, R.style.dialog)
            val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_updata_pro, null)
            mDilog!!.setContentView(view)
            mDilog!!.setCancelable(false)
            mDilog!!.show()
            bar = view.findViewById<ProgressBar>(R.id.updata_pro_bar)
        }

    }
    private fun bindService(){
        val intent = Intent()
        intent.setPackage("com.liyaan.mynew");
        intent.action = "com.liyaan.mynew.downaction";
        bindService(intent, connection, Context.BIND_AUTO_CREATE);



    }
    private fun getWechatApi() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            val cmp = ComponentName(
                "com.tencent.mm",
                "com.tencent.mm.ui.LauncherUI"
            )
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.component = cmp
            startActivity(intent)
            Handler(mainLooper).postDelayed(object:Runnable{
                override fun run() {

                }

            },2000)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "检查到您手机没有安装微信，请安装后使用该功能",Toast.LENGTH_SHORT).show()
        }
    }

}