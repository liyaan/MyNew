package com.liyaan.mvvm.net.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import com.liyaan.mynew.R


abstract class BaseFragment: Fragment() {

    var unbinder: Unbinder? = null
    var viewLayout: View? = null
    var mActivity: Activity? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity
    }

    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        viewLayout = inflater.inflate(initView(), container, false)
        unbinder = ButterKnife.bind(this, viewLayout!!)
        return viewLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }


    protected abstract fun initData()

    protected abstract fun initView(): Int

    open fun initToolbar(
        toolbar: Toolbar?,
        textView: TextView?,
        title: String?,
        isBack: Boolean
    ) {
        if (textView != null && title != null) {
            textView.text = title
        }
        if (toolbar != null) {
            toolbar.setTitle("")
            if (isBack) {
                toolbar.setNavigationIcon(R.drawable.ic_left)
                toolbar.setNavigationOnClickListener(View.OnClickListener { mActivity!!.finish() })
            }
        }
    }


    open fun startActivity(activity: Activity?, cls: Class<out Activity?>?) {
        val intent = Intent()
        intent.setClass(activity!!, cls!!)
        startActivity(intent)
        mActivity!!.overridePendingTransition(R.animator.up_in, R.animator.up_out)
    }

    open fun startActivity(
        activity: Activity?,
        cls: Class<out Activity?>?,
        bundle: Bundle?
    ) {
        if (activity != null && cls != null && bundle != null) {
            val intent = Intent()
            intent.putExtras(bundle)
            intent.setClass(activity, cls)
            startActivity(intent)
            mActivity!!.overridePendingTransition(R.animator.up_in, R.animator.up_out)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }
}