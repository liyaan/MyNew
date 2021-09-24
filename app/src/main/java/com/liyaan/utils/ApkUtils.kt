package com.liyaan.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File

object ApkUtils {
    private val TAG = ApkUtils::class.java.simpleName

    /**
     * 获取应用程序名称
     */
    fun getAppName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val packageInfo =
                packageManager.getPackageInfo(context.packageName, 0)
            val labelRes = packageInfo.applicationInfo.labelRes
            return context.resources.getString(labelRes)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取应用程序版本名称信息
     *
     * @param context
     * @return 当前应用的版本名称
     */
    fun getVersionName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val packageInfo =
                packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return null
    }

    /**
     * @return 当前程序的版本号
     */
    fun getVersionCode(context: Context): Int {
        val version: Int
        version = try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
        return version
    }

    /**
     * 得到安装的intent
     * @param apkFile
     * @return
     */
    fun getInstallIntent(apkFile: File?, activity: Activity?): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider
                .getUriForFile(activity!!, "com.liyaan.mynew.fileprovider", apkFile!!)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(
                Uri.fromFile(apkFile),
                "application/vnd.android.package-archive"
            )
        }
        return intent
    }

    fun getInstall(apkFile: File?, activity: Activity?) {
        Log.i("aaaa","ddsfdfadfdasfsd1")
        val install = Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:com.liyaan.mynew")
        )
        install.action = Intent.ACTION_VIEW
        install.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        install.addCategory(Intent.CATEGORY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //大于Android版本7.0
            //这里的"com.anxin.provider"需要和AndroidManifest.xml的provider属性保持一致
            val contentUri = FileProvider.getUriForFile(
                activity!!,
                "com.liyaan.mynew.fileprovider",
                apkFile!!
            )
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            install.setDataAndType(contentUri, "application/vnd.android.package-archive")
            activity.startActivity(install)
            Log.i("aaaa","ddsfdfadfdasfsd3")
        } else {
            try {
                Log.i("aaaa","ddsfdfadfdasfsd2")
                install.setDataAndType(
                    Uri.fromFile(apkFile!!),
                    "application/vnd.android.package-archive"
                )
                activity!!.startActivity(install)
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

    // 两次点击间隔不能少于1000ms
    private const val FAST_CLICK_DELAY_TIME = 1500
    private var lastClickTime: Long = 0
    val isFastClick: Boolean
        get() {
            var flag = true
            val currentClickTime = System.currentTimeMillis()
            if (currentClickTime - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                flag = false
            }
            lastClickTime = currentClickTime
            return flag
        }

    // 判断是否打开了通知监听权限
    fun isEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

}