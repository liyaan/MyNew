package com.liyaan.mynew

import android.Manifest
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.liyaan.download.DownloadListener
import com.liyaan.mynew.DownloadService.DownloadBinder
import com.liyaan.rxJava.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class DownloadActivity:AppCompatActivity(), View.OnClickListener {
    private var downloadBinder: DownloadBinder? = null
    var mDilog:Dialog? = null
    var bar:ProgressBar? = null
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            downloadBinder = service as DownloadBinder
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        val startDownload: Button = findViewById(R.id.start_download)
        val pauseDownload: Button = findViewById(R.id.pause_download)
        val cancelDownload: Button = findViewById(R.id.cancel_download)
        val openWea: Button = findViewById(R.id.open_weacher)
        startDownload.setOnClickListener(this)
        pauseDownload.setOnClickListener(this)
        cancelDownload.setOnClickListener(this)
        openWea.setOnClickListener(this)
        val intent = Intent(this, DownloadService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }

        Observable.create(object:OnSubscribe<String>{
            override fun call(subscriber: Subscriber<in String>) {
                Log.i("OnSub ",Thread.currentThread().name);
//                for (i in 0..3){
//                    subscriber.onNext("$i")
//                }
                subscriber.onNext("http://vipapp.tun.aitifen.cn/index/user/appVersion?type=android")
            }

        }).observeOn(Schedulers.io()).map(object:Transformer<String,String>{
            override fun call(from: String): String {
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url(from)
                    .build()
                val response: Response = client.newCall(request).execute()
                return response.body()!!.string()
            }

        }).observeOnMain(AndroidSchedulers.mianRun()).subscribe(object:Subscriber<String>(){
            override fun onCompleted() {
            }

            override fun onError(t: Throwable) {
            }

            override fun onNext(t: String) {
                Log.i("Subscriber@ ",Thread.currentThread().getName());
                Log.i("aaaa",t)
            }

        })
    }

    override fun onClick(v: View) {
        if (downloadBinder == null) {
            return
        }
        when (v.id) {
            R.id.start_download -> {
                //                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                val url =
                    "http://download.aitifen.com/android/gs1v1-release.apk"
                downloadBinder?.startDownload(url)
                downloadBinder?.setListener(object: DownloadListener {
                    override fun onProgress(progress: Int) {
                        showDialog()
                        bar?.progress = progress
                    }

                    override fun onSuccess() {
                        if (mDilog!=null && mDilog!!.isShowing){
                            mDilog?.dismiss()
                        }
                    }

                    override fun onFail() {
                        if (mDilog!=null && mDilog!!.isShowing){
                            mDilog?.dismiss()
                        }
                    }

                    override fun onPaused() {
                    }

                    override fun onCanceled() {
                    }

                })
            }
            R.id.pause_download -> downloadBinder!!.pauseDownload()
            R.id.cancel_download -> downloadBinder!!.cancelDownload()
            R.id.open_weacher->getWechatApi()
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

    override fun onDestroy() {
        super.onDestroy()
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