package com.liyaan.glide.load.model

import com.liyaan.glide.load.Encoder
import com.liyaan.glide.load.engine.bitmap_recycle.ArrayPool
import java.io.File
import java.io.InputStream
import com.liyaan.glide.load.Options
import java.io.FileOutputStream
import java.io.OutputStream

class StreamEncoder(val byteArrayPool: ArrayPool) : Encoder<InputStream> {

    //todo encode File
    override fun encode(data: InputStream, file: File, options: Options): Boolean {
        val buffer = byteArrayPool[ArrayPool.STANDARD_BUFFER_SIZE_BYTES, ByteArray::class.java]
        val os: OutputStream
        var success = false
        try {
            os = FileOutputStream(file)
            var read = 0
            while ((data.read(buffer)).also { read = it } != -1) {
                os.write(buffer, 0, read)
            }
            os.close()
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            byteArrayPool.put(buffer)
        }
        return success
    }

}