package com.liyaan.mynew

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.liyaan.eventBus.EventBus

class TestActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_test)
        val textView = findViewById<TextView>(R.id.textView)
        textView.setOnClickListener {
            EventBus.getDefault().post("返回上一页")
            finish()
        }

    }
}