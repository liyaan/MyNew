package com.liyaan.mvvm.net.ui.activity

import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import com.google.android.material.tabs.TabLayout
import com.liyaan.mynew.R


class MvvmMainActivity:BaseActivity() {

    @BindView(R.id.title)
    lateinit var mTitle: TextView

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar

    @BindView(R.id.tab_layout)
    lateinit var mTabLayout: TabLayout

    @BindView(R.id.view_Pager)
    lateinit var mViewPager: ViewPager
    private val mTitles =
        arrayOf("头条", "社会", "国内", "国际", "娱乐")
    var fragmentList: MutableList<Fragment> = ArrayList()

    override fun ininLayout(): Int {
        return R.layout.activity_mvvm_main_layout
    }

    override fun initView() {
        initToolbar(mToolbar,mTitle,"新闻",false)

        mTitles.forEachIndexed { index, s ->
            Log.i("aaaaaaaaaaaaaa",s)
            fragmentList.add(NewsFragment.newInstance(index))
        }
        ShowToast("${fragmentList.size}")
        mViewPager.adapter =MyApdater(supportFragmentManager)
        mTabLayout.setupWithViewPager(mViewPager)
    }
    inner class MyApdater(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }
    }
}