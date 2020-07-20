package com.example.myapplication.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.action_bar_layout.*
import kotlinx.android.synthetic.main.action_bar_layout.view.*
import kotlinx.android.synthetic.main.action_bar_layout.view.titleTextview
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.view.tabLayout
import kotlinx.android.synthetic.main.fragment_movie.view.viewPager

class AlbumsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_movie)
        init()
    }

    private fun init() {
        titleTextview?.text = "Albums"
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Tracks"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Albums"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Playlists"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Artists"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = ViewPagerAdapter(supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }
}