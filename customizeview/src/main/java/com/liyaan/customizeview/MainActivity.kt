package com.liyaan.customizeview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.liyaan.customizeview.fragment.MarkFragment
import com.liyaan.customizeview.fragment.RadarFragment
import com.liyaan.customizeview.fragment.StepViewFragment
import com.liyaan.customizeview.fragment.StickyRecyclerViewFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var titles = arrayOf("评分", "城市列表", "时光轴", "雷达图")

    var fragments = arrayListOf(
        MarkFragment(), StickyRecyclerViewFragment(),
        StepViewFragment(), RadarFragment()
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        viewpager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }
        }

        tablayout.setupWithViewPager(viewpager)
    }
}
