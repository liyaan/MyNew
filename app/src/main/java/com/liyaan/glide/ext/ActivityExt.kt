package com.liyaan.glide.ext

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun Activity.assertNotDestroyed() {
    if(isDestroyed){
        throw IllegalArgumentException("You cannot start a load for a destroyed activity")
    }
}
fun Activity.isActivityVisible() =!isFinishing