package com.liyaan.glide

import androidx.annotation.CheckResult

interface ModelTypes<T> {


    @CheckResult
    fun load(string: String): T
}