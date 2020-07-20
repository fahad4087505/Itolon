package com.example.myapplication.activities

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.R
import com.example.myapplication.fragments.*
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.ViewUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {
    private var mReceiver: BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
    private fun init() {
        if (SharedPref.read(SharedPref.USER_TYPE, "").equals("artist")) {
            addFragment(StatisticsFragment(), false, "one")
            bottom_navigation.inflateMenu(R.menu.artist_menu)
        } else if (SharedPref.read(SharedPref.USER_TYPE, "").equals("executive")) {
            addFragment(StatisticsFragment(), false, "one")
            bottom_navigation.inflateMenu(R.menu.executive_menu)
        } else {
            addFragment(FeedFragment(), false, "one")
            bottom_navigation.inflateMenu(R.menu.bottom_navigation_menu)
        }
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
            }
            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) { /* ... */
            }
        }).check()
    }
    var navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    addFragment(FeedFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_search -> {
                    addFragment(SearchFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    addFragment(SettingFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    addFragment(ArtistProfileFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_statistics -> {
                    addFragment(StatisticsFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun addFragment(fragment: Fragment, addToBackStack: Boolean, tag: String) {
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        if (addToBackStack) {
            ft.addToBackStack(tag)
        }
        ft.replace(R.id.container_frame, fragment, tag)
        ft.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        ViewUtils.showMaterialDialog(this@MainActivity,"Are you sure you want to close this application?").subscribe{ res->
            if(res){
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mReceiver != null) {
                this.unregisterReceiver(this.mReceiver);
            }
        } catch (ignored: Exception) {
        }
    }
    override fun onStop() {
        super.onStop()
    }
    override fun onClick(v: View?) {
    }
}