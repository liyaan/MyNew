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
import com.liyaan.mynew.DownloadService.DownloadBinder
import com.liyaan.utils.ApkUtils
import kotlinx.android.synthetic.main.activity_download.*
import java.io.File


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
        startDownload.setOnClickListener(this)
        pauseDownload.setOnClickListener(this)
        cancelDownload.setOnClickListener(this)
        openWea.setOnClickListener(this)
        clickBtn.setOnClickListener(this)
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
//        toInStallPermissionSettingActivity()
//        Observable.create(object:OnSubscribe<String>{
//            override fun call(subscriber: Subscriber<in String>) {
//                Log.i("OnSub ",Thread.currentThread().name);
////                for (i in 0..3){
////                    subscriber.onNext("$i")
////                }
//                subscriber.onNext("")
//            }
//
//        }).observeOn(Schedulers.io()).map(object:Transformer<String,String>{
//            override fun call(from: String): String {
//                if (from!=null){
//                    val client = OkHttpClient()
//                    val JSON: MediaType? = MediaType.parse("application/json;charset=utf-8")
//                    val json = JSONObject()
//                    json.put("page","1")
//                    json.put("page_size","20")
//                    val respuestBody = RequestBody.create(JSON,json.toString())
//                    val request: Request = Request.Builder()
//                        .url(from).post(respuestBody)
//                        .build()
//                    val response: Response = client.newCall(request).execute()
//                    return response.body()!!.string()
//                }else{
//                    return "url不能为空"
//                }
//
//            }
//
//        }).observeOnMain(AndroidSchedulers.mianRun()).subscribe(object:Subscriber<String>(){
//            override fun onCompleted() {
//            }
//
//            override fun onError(t: Throwable) {
//            }
//
//            override fun onNext(t: String) {
//                Log.i("Subscriber@ ",Thread.currentThread().getName());
//                Log.i("aaaa",t)
//            }
//
//        })
    }

    override fun onClick(v: View) {
        if (downloadBinder == null) {
            return
        }
        when (v.id) {
            R.id.start_download -> {
                //                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                val url =
                    "http://gdown.baidu.com/data/wisegame/57a788487345e938/QQ_358.apk"
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
                        Log.i("aaaaaaaaa","bbbbbbbbbb")
                        val filename = url.substring(url.lastIndexOf("/"))
                        val directory: String =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .path
                        val file = File(directory + filename)
                        ApkUtils.getInstall(file,
                            this@DownloadActivity)
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
            R.id.click_btn_text->{
                val intent = Intent(this,TestActivity::class.java)
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun toInStallPermissionSettingActivity() {
        val packageURI = Uri.parse("package:" + getPackageName())

//注意这个是8.0新API
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
//        startActivityForResult(intent, REQUEST_CODE_INSTALL_PERMISSION)
        startActivity(intent)
    }
//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_INSTALL_PERMISSION) {
//            checkInstall() //以防万一，再次检查权限
//        }
//    }
}