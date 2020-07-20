package com.example.myapplication.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.fragments.AlbumDetailFragment

class ViewPagerAdapter(fm: FragmentManager, internal var totalTabs: Int) :
    FragmentPagerAdapter(fm) {
    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return AlbumDetailFragment()
            }
            1 -> {
                return AlbumDetailFragment()
            }
            2 -> {
                return AlbumDetailFragment()
            }
            3 -> {
                return AlbumDetailFragment()
            }
            else -> return AlbumDetailFragment()
        }
    }
    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}