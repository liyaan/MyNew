package com.liyaan.mvvm.net.ui.activity

import android.app.Activity
import java.util.*


class ActivityStackManager {
    private var mActivityStack: Stack<Activity>? = null

    /**
     * 添加一个Activity到堆栈中 * @param activity
     */
    fun addActivity(activity: Activity?) {
        if (null == mActivityStack) {
            mActivityStack = Stack()
        }
        mActivityStack?.add(activity)
    }

    /**
     * 从堆栈中移除指定的Activity * @param activity
     */
    fun removeActivity(activity: Activity?) {
        if (activity != null) {
            mActivityStack?.remove(activity)
        }
    }

    /**
     * 获取顶部的Activity * @return 顶部的Activity
     */
    fun getTopActivity(): Activity? {
        return if (mActivityStack!!.isEmpty()) {
            null
        } else {
            mActivityStack?.get(mActivityStack!!.size - 1)
        }
    }

    /**
     * 结束所有的Activity，退出应用
     */
    fun removeAllActivity() {
        if (mActivityStack != null && mActivityStack!!.size > 0) {
            for (activity in mActivityStack!!) {
                activity.finish()
            }
        }
    }

}