package com.cs.common.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.log(msg: String) = Log.e("tag", msg)
