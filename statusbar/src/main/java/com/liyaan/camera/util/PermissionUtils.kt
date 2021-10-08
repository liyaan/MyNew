package com.liyaan.camera.util

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val PERMISSION_REQUEST_CODE = 100
    const val PERMISSION_SETTING_CODE = 101

    private var permissionExplainDialog: AlertDialog? = null
    private var permissionSettingDialog: AlertDialog? = null

    fun checkPermission(activity:AppCompatActivity,
                        permissionList:Array<String>,callback:Runnable){
        var allGranted = true
        permissionList.forEach {
            val result = ContextCompat.checkSelfPermission(activity,it)
            Log.i("aaa","检查权限 $it   结果 $result")
            if (result!=PackageManager.PERMISSION_GRANTED){
                allGranted = false
                Log.i("aaa","检查权限 $it   结果 $result  获取失败")
            }
        }
        if (allGranted){
            Log.i("aaa","$allGranted  aaaaaaaa")
            callback.run()
        }else{
            Log.i("aaa","$allGranted  ddddddddddddd")
            startRequestPermission(activity, permissionList)
        }
    }

    private fun startRequestPermission(activity: AppCompatActivity,
                                       permissionList: Array<String>){
        permissionList.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,it)){
                showPermissionExplainDialog(activity, permissionList)
            }else{
                requestPermission(activity, permissionList)
            }
        }
    }

    private fun showPermissionExplainDialog(activity: AppCompatActivity,
                                            permissionList: Array<String>){
        if (permissionExplainDialog == null){
            permissionExplainDialog = AlertDialog.Builder(activity).setTitle("权限申请")
                .setMessage("您刚才拒绝了相关权限，但是现在应用需要这个权限，" +
                        "点击确定申请权限，点击取消将无法使用该功能")
                .setPositiveButton("确定") { dialog, _ ->
                    requestPermission(activity, permissionList)
                    dialog.cancel()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }
        permissionExplainDialog?.let {
            if (!it.isShowing){
                it.show()
            }
        }
    }
    /**
     *  不需要向用户解释了，我们可以直接请求该权限
     *  第三步. 请求权限
     */
    private fun requestPermission(activity: AppCompatActivity, permissionList: Array<String>) {
        ActivityCompat.requestPermissions(activity, permissionList, PERMISSION_REQUEST_CODE)
    }

    fun showPermissionSettingDialog(activity: AppCompatActivity) {
        if (permissionSettingDialog == null) {
            permissionSettingDialog = AlertDialog.Builder(activity)
                .setTitle("权限设置")
                .setMessage("您刚才拒绝了相关的权限，请到应用设置页面更改应用的权限")
                .setPositiveButton("确定") { dialog, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivityForResult(intent, PERMISSION_SETTING_CODE)
                    dialog.cancel()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.cancel()
                }
                .create()

        }

        permissionSettingDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }
}