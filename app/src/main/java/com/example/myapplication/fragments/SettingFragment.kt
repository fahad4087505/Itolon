package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.colepower.view.CustomProgressBar
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.*
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.profilemodel.Profile
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.ProfileViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.profile_image
import org.json.JSONObject

class SettingFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    val progressBar = CustomProgressBar()
    private var mView:View ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_settings, container, false)
        profileViewModel = ViewModelProvider.NewInstanceFactory().create(ProfileViewModel::class.java)
        init(mView!!)
        return mView
    }

    private fun init(view: View) {
        mView!!.user_name_textview.text=SharedPref.read(SharedPref.USER_NAME,"")
        mView!!.post_layout.setOnClickListener {
            startActivity(Intent(activity, PostsActivity::class.java))
        }
        val hashMap = HashMap<String, String>()
        hashMap["outh_token"] =SharedPref.read(SharedPref.AUTH_TOKEN,"")
        hashMap["device_token"] =SharedPref.read(SharedPref.REFRESH_TOKEN,"")
        hashMap["user_id"] =SharedPref.read(SharedPref.USER_ID,"")
        profileViewModel.getUserProfile(hashMap)
        getResponse()
        progressBar.show(activity as MainActivity)
        view.logout_textview.setOnClickListener {
            ViewUtils.showMaterialDialog(requireContext(),"Are you sure you want to logout?").subscribe{ res->
                if(res){
                    if (Utils.getInstance().isNetworkConnected(requireActivity())) {
                        logoutUser()
                    } else{
                        showErrorDialog("No Internet Connection")
                    }
                }
            }

        }
        view.my_playlist_layout.setOnClickListener {
            startActivity(Intent(requireActivity(),MyPlaylistActivity::class.java))
        }
    }
    private fun logoutUser(){
        val hashMap = java.util.HashMap<String, String>()
        hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
        hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
        profileViewModel.logoutUser(hashMap)
        progressBar.show(activity!!)
        getLogoutResponse()
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(requireContext(),message,false,"Retry").subscribe{res->
            if(res){
                logoutUser()
            }
        }
    }
    private fun getResponse() {
        profileViewModel.profileLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Profile::class.java)
                if (response.meta.code == 205) {
                    mView!!.user_name_textview.text=response.result.name +" "+response.result.secondName
                    Glide.with(activity!!).load("http://44.231.47.188"+response.result.photo).error(R.drawable.logo).placeholder(R.drawable.logo).into(profile_image)
                }
            }
        })
    }
    private fun getLogoutResponse() {
        profileViewModel.logoutLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), DefaultModel::class.java)
                if (response.meta.code == 233) {
                    SharedPref.clear()
                    finishAffinity(activity!!)
                    startActivity(Intent(activity, LoginActivity::class.java))
                }else{
                    showErrorDialog(response.meta.message.toString())
                }
            }else{
                ViewUtils.showSnackBar(requireContext(),"Server is not responding",true,"")
            }
        })
    }
}