package com.liyaan.intentService

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.mynew.R
import kotlinx.android.synthetic.main.activity_intent_service.*

class IntentServiceActivity:AppCompatActivity(), MyIntentService.UpdateUI {
    private val urls:Array<String> = arrayOf(
        "https://img-blog.csdn.net/20160903083245762",
        "https://img-blog.csdn.net/20160903083252184",
        "https://img-blog.csdn.net/20160903083257871",
        "https://img-blog.csdn.net/20160903083257871",
        "https://img-blog.csdn.net/20160903083311972",
        "https://img-blog.csdn.net/20160903083319668",
        "https://img-blog.csdn.net/20160903083326871",
        "https://img2.baidu.com/it/u=3298500332,531872234&fm=26&fmt=auto",
        "https://img0.baidu.com/it/u=4230649785,3908633230&fm=26&fmt=auto",
        "https://img2.baidu.com/it/u=1305186920,1181788656&fm=26&fmt=auto&gp=0.jpg"
    )
    private var updateBool: Boolean = true
    private val  mUIHandler = object: Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            image.setImageBitmap(msg.obj as Bitmap)
            if (msg.what == urls.size-1){
                updateBool = false
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent_service)
        val intent = Intent(this,MyIntentService::class.java)
        urls.forEachIndexed { index, s ->
            intent.putExtra("download_url",s)
            intent.putExtra("index_flag",index)
            startService(intent)
        }
        MyIntentService.updateUI = this
        image.setOnClickListener {
            Log.i("aaaa","$updateBool")
            if (!updateBool){
                updateBool = true
                val intent = Intent(this,MyIntentService::class.java)
                urls.forEachIndexed { index, s ->
                    intent.putExtra("download_url",s)
                    intent.putExtra("index_flag",index)
                    startService(intent)
                }
            }

        }
    }

    override fun updateMsgUI(message: Message) {
        mUIHandler.sendMessageDelayed(message, (message.what * 1000).toLong())
        Log.i("aaaa","${message.what} ${urls.size}")

    }
}