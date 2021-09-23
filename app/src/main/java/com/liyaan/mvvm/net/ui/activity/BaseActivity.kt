package com.liyaan.mvvm.net.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.ButterKnife
import butterknife.Unbinder
import com.liyaan.mynew.R


abstract class BaseActivity: AppCompatActivity() {
    private var context: Context? = null
    private var unbinder: Unbinder? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ininLayout())
        context = this
        unbinder = ButterKnife.bind(this)
        initView()
    }

    fun getContext(): Context? {
        return context
    }

    protected abstract fun ininLayout(): Int

    protected abstract fun initView()

    fun initToolbar(
        toolbar: Toolbar?,
        textView: TextView?,
        title: String?,
        isBack: Boolean
    ) {
        if (textView != null && title != null) {
            textView.text = title
        }
        if (toolbar != null) {
            toolbar.title = ""
            if (isBack) {
                toolbar.setNavigationIcon(R.drawable.ic_left)
                toolbar.setNavigationOnClickListener(View.OnClickListener { finish() })
            }
        }
    }

    fun ShowToast(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun startActivity(activity: Activity?, cls: Class<out Activity?>?) {
        val intent = Intent()
        intent.setClass(activity!!, cls!!)
        startActivity(intent)
        overridePendingTransition(R.animator.up_in, R.animator.up_out)
    }

    fun startActivity(
        activity: Activity?,
        cls: Class<out Activity?>?,
        bundle: Bundle?
    ) {
        if (activity != null && cls != null && bundle != null) {
            val intent = Intent()
            intent.putExtras(bundle)
            intent.setClass(activity, cls)
            startActivity(intent)
            overridePendingTransition(R.animator.up_in, R.animator.up_out)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.animator.down_in, R.animator.down_out)
    }
}