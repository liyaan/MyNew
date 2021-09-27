package com.liyaan.mynew

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.liyaan.download.DownloadListener
import com.liyaan.download.DownloadTask
import com.liyaan.utils.MD5Util
import com.liyaan.utils.MD5Util.isBackground
import java.io.File


class DownloadAidlService:Service(){
    private var downloadTask: DownloadTask? = null
    private var downloadUrl: String? = null
    private var mlistener:DownloadAidlListener? = null
    private val listener: DownloadListener =
        object : DownloadListener {
            override fun onProgress(progress: Int) {
                if (mlistener!=null){
                    mlistener?.onProgress(progress)
                }
                getNotificationManager().notify(1, getNotification("Downloading...", progress))
            }

            override fun onSuccess() {
                if(isBackground(this@DownloadAidlService)){
                    val intent = packageManager.getLaunchIntentForPackage(packageName);
                    intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    Log.i("aaaa",
                        "aaddddff${isBackground(this@DownloadAidlService)} $packageName")
                    startActivity(intent)
                }
//                MD5Util.isRunningForegroundToApp1(this@DownloadService,DownloadActivity::class.java)
                if (mlistener!=null){
                    mlistener?.onSuccess()
                }
                downloadTask = null
                //下载成功关闭前台服务通知，并创建一个下载成功的通知
                stopForeground(true)
                getNotificationManager().notify(1, getNotification("Download Success", -1))
                Toast.makeText(this@DownloadAidlService, "DownloadSuccess", Toast.LENGTH_SHORT).show()
            }

            override fun onFail() {
                if (mlistener!=null){
                    mlistener?.onFail()
                }
                downloadTask = null
                //下载失败关闭前台服务通知，并创建一个下载失败的通知
                stopForeground(true)
                getNotificationManager().notify(1, getNotification("Download Failed", -1))
                Toast.makeText(this@DownloadAidlService, "DownloadFailed", Toast.LENGTH_SHORT).show()
            }

            override fun onPaused() {
                downloadTask = null
                Toast.makeText(this@DownloadAidlService, "DownloadPaused", Toast.LENGTH_SHORT).show()
            }

            override fun onCanceled() {
                downloadTask = null
                stopForeground(true)
                Toast.makeText(this@DownloadAidlService, "DownloadCancel", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onBind(intent: Intent?): IBinder? {
        return buildService
    }
    private val buildService = object:DownAidlService.Stub(){
        override fun pauseDownload() {
            if (downloadTask != null) {
                downloadTask?.pauseDownload()
            }
        }

        override fun startDownload(url: String?) {
            if (downloadTask == null) {
                downloadUrl = url
                downloadTask = DownloadTask(listener)
                downloadTask?.execute(downloadUrl)
                startForeground(1, getNotification("Downloading...", 0))
            }
        }

        override fun setListener(lis: DownloadAidlListener?) {
            mlistener = lis
        }

        override fun cancelDownload() {
            if (downloadTask != null) {
                downloadTask?.cancelDownload()
            } else {
                if (downloadUrl != null) {
                    val filename: String =
                        downloadUrl!!.substring(downloadUrl!!.lastIndexOf("/"))
                    val directory: String =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .path
                    val file = File(directory + filename)
                    if (file.exists()) {
                        file.delete()
                    }
                    getNotificationManager().cancel(1)
                    stopForeground(true)
                }
            }
        }

    }
    private fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun getNotification(title: String, progress: Int): Notification? {

        val ID = "com.liyaan.mynew" //这里的id里面输入自己的项目的包的路径

        val NAME = "Channel One"
        val intent = Intent(this, DownloadActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC;
            manager.createNotificationChannel(channel);
            notification =  NotificationCompat.Builder(this).setChannelId(ID);
        } else {
            notification =  NotificationCompat.Builder(this);
        }
//        val builder = NotificationCompat.Builder(this, "007")
        notification.setSmallIcon(R.mipmap.ic_launcher)
        notification.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        notification.setContentIntent(pi)
        notification.setContentTitle(title)
        if (progress > 0) {
            notification.setContentText("$progress%")
            notification.setProgress(100, progress, false)
        }
        return notification.build()
    }

}