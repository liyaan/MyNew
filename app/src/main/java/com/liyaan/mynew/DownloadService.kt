package com.liyaan.mynew

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.liyaan.download.DownloadListener
import com.liyaan.download.DownloadTask
import java.io.File


class DownloadService:Service(){
    private var downloadTask: DownloadTask? = null
    private var downloadUrl: String? = null
    private var mBinder = DownloadBinder()
    private var mlistener:DownloadListener? = null
    private val listener: DownloadListener =
        object : DownloadListener {
            override fun onProgress(progress: Int) {
                if (mlistener!=null){
                    mlistener?.onProgress(progress)
                }
                getNotificationManager().notify(1, getNotification("Downloading...", progress))
            }

            override fun onSuccess() {
                if (mlistener!=null){
                    mlistener?.onSuccess()
                }
                downloadTask = null
                //下载成功关闭前台服务通知，并创建一个下载成功的通知
                stopForeground(true)
                getNotificationManager().notify(1, getNotification("Download Success", -1))
                Toast.makeText(this@DownloadService, "DownloadSuccess", Toast.LENGTH_SHORT).show()
            }

            override fun onFail() {
                if (mlistener!=null){
                    mlistener?.onFail()
                }
                downloadTask = null
                //下载失败关闭前台服务通知，并创建一个下载失败的通知
                stopForeground(true)
                getNotificationManager().notify(1, getNotification("Download Failed", -1))
                Toast.makeText(this@DownloadService, "DownloadFailed", Toast.LENGTH_SHORT).show()
            }

            override fun onPaused() {
                downloadTask = null
                Toast.makeText(this@DownloadService, "DownloadPaused", Toast.LENGTH_SHORT).show()
            }

            override fun onCanceled() {
                downloadTask = null
                stopForeground(true)
                Toast.makeText(this@DownloadService, "DownloadCancel", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class DownloadBinder() : Binder() {
        fun setListener(lis:DownloadListener){
            mlistener = lis
        }
        fun startDownload(url: String) {
            if (downloadTask == null) {
                downloadUrl = url
                downloadTask = DownloadTask(listener)
                downloadTask?.execute(downloadUrl)
                startForeground(1, getNotification("Downloading...", 0))
                Toast.makeText(this@DownloadService, "Downloading...", Toast.LENGTH_SHORT).show()
            }
        }

        fun pauseDownload() {
            if (downloadTask != null) {
                downloadTask?.pauseDownload()
            }
        }

        fun cancelDownload() {
            if (downloadTask != null) {
                downloadTask?.cancelDownload()
            } else {
                if (downloadUrl != null) {
                    val filename: String =
                        downloadUrl!!.substring(downloadUrl!!.lastIndexOf("/"))
                    val directory: String =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getPath()
                    val file = File(directory + filename)
                    if (file.exists()) {
                        file.delete()
                    }
                    getNotificationManager().cancel(1)
                    stopForeground(true)
                    Toast.makeText(this@DownloadService, "Canceled...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun getNotification(title: String, progress: Int): Notification? {

        val ID = "com.example.service1" //这里的id里面输入自己的项目的包的路径

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
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
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