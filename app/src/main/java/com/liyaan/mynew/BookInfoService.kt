package com.liyaan.mynew

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class BookInfoService:Service() {

    private var bookList: ArrayList<BookInfo>? = null

    override fun onCreate() {
        super.onCreate()
        bookList = ArrayList()
        initData()
    }
    private fun initData() {
        val book1 = BookInfo("活着","张三","2021-09-18")
        val book2 = BookInfo("或者","李四","2021-09-18")
        val book3 = BookInfo("叶应是叶","王五","2021-09-18")
        bookList!!.add(book1)
        bookList!!.add(book2)
        bookList!!.add(book3)
    }
    override fun onBind(intent: Intent?): IBinder? {
        Log.i("aaaaaa","a2222222222222222222")
        return sub
    }
    private val sub:BookService.Stub = object:BookService.Stub(){
        override fun getBookList(): BookInfo {
            Log.i("aaaaaa","111111111111111111")
            return BookInfo("活着","张三","2021-09-18")
        }

        override fun addBookInOut(book: BookInfo?) {
            book?.let {
                Log.i("aaaaa","${book.bookName} - ${book.bookAuthor} - ${book.bookYear}")
            }?:let {
                Log.i(this.javaClass.name,"不能为空")
            }
        }

    }
}