package com.liyaan.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.liyaan.mynew.BookInfo
import com.liyaan.mynew.BookService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var bookService: BookService? = null

    private var connected = false

    private var bookList: BookInfo? = null

    private val serviceConnection = object:ServiceConnection{
        override fun onServiceDisconnected(p0: ComponentName?) {
            connected = false
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            bookService = BookService.Stub.asInterface(p1)
            // 注册死亡代理
            if (bookService != null) {
                p1?.linkToDeath(mDeathRecipient, 0)
            }
            connected = true
        }

    }

    private val mDeathRecipient = DeathRecipient {
        // 当绑定的service异常断开连接后，自动执行此方法
        if (bookService != null) {
            // 当前绑定由于异常断开时，将当前死亡代理进行解绑        mIMyAidlInterface.asBinder().unlinkToDeath(mDeathRecipient, 0);
            //  重新绑定服务端的service
            bindService()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindService()

        btn_getBookList.setOnClickListener {
            if (connected && bookService?.asBinder()?.isBinderAlive!!) {
                try {
                    bookList = bookService?.bookList
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
                log()
            }
        }
        btn_addBook_inOut.setOnClickListener {
            if (connected && bookService?.asBinder()?.isBinderAlive!!) {
                val book = BookInfo("一本书","名字","2019-09-08")
                try {
                    bookService?.addBookInOut(book)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun bindService(){
        val intent = Intent()
        intent.setPackage("com.liyaan.mynew");
        intent.action = "com.liyaan.mynew.action";
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);



    }

    private fun log(){
        bookList?.apply {
            Log.i("aaaa","${bookName}  ${bookAuthor}  ${bookYear}")
        }
    }

    override fun onDestroy() {
        if (connected) {
            unbindService(serviceConnection)
        }
        super.onDestroy()
    }
}
