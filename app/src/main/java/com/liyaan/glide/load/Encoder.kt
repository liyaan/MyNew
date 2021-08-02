package com.liyaan.glide.load

import java.io.File

interface Encoder<T>{
    fun encode(data :T, file : File, options:Options):Boolean
}