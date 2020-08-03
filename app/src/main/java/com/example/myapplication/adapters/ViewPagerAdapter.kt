package com.example.myapplication.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.fragments.AlbumDetailFragment
import com.example.myapplication.model.categoriessongmodel.Result
import com.example.myapplication.model.categoriessongmodel.Song
import com.example.myapplication.prefrences.Constants

class ViewPagerAdapter(fm: FragmentManager, internal var totalTabs: Int ,internal var resultList:List<Result>) :
    FragmentPagerAdapter(fm) {
    // this is for fragment tabs
//    override fun getItem(position: Int): Fragment {
//        return AlbumDetailFragment()
//
//    }

    override fun getItem(position: Int): Fragment {
        return AlbumDetailFragment(resultList[position].songs as ArrayList<Song>)
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}