package com.liyaan.utils;



import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.security.MessageDigest;
import java.util.List;

/**
 * MD5工具类
 */
public class MD5Util {

    public final static String md5Lower(String s){
        String md5 = getMd5(s);
//        return StringUtils.isNotEmpty(md5)? md5.toLowerCase() : md5;
        return md5.toLowerCase();
    }
    public final static String encodeMd5(String url){
        String md5 = getMd5(url);
        return md5.toLowerCase();
    }
    public final static String getMd5(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    public static String byteToHexStringNoUpper(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    /** 判断是否处于后台
     * @param context
     * @return true：处于后台, false：不处于后台
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if(appProcesses == null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
//    @SuppressLint("NewApi")
//    public static void isRunningForegroundToApp1(Context context, final Class Class) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(20);
//        /**枚举进程*/
//
//        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
//            //*找到本应用的 task，并将它切换到前台
//            if (taskInfo.baseActivity.getPackageName().equals(context.getPackageName())) {
//                Log.e("timerTask", "timerTask  pid " + taskInfo.id);
//                Log.e("timerTask", "timerTask  processName " + taskInfo.topActivity.getPackageName());
//                Log.e("timerTask", "timerTask  getPackageName " + context.getPackageName());
//                activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
//                Intent intent = new Intent(context, Class);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                intent.setAction(Intent.ACTION_MAIN);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                context.startActivity(intent);
//                break;
//            }
//        }
//    }
}
