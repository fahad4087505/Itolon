package com.example.myapplication.activities
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.createplaylistmodel.CreatePlaylistModel
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.CreatePlaylistViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_playlist.*
import org.json.JSONObject

class CreatePlaylistActivity : BaseActivity() {
    private lateinit var createPlaylistViewModel: CreatePlaylistViewModel
    var snackbarwithbutton: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createPlaylistViewModel = ViewModelProvider.NewInstanceFactory().create(CreatePlaylistViewModel::class.java)
        setContentView(R.layout.activity_create_playlist)
        back_imageview.setOnClickListener { finish() }
        create_playlist_textview.setOnClickListener {
            if(playlist_name_edittext.text.toString().isNullOrEmpty()){
                ViewUtils.showSnackBar(this@CreatePlaylistActivity,"Please Enter Playlist Name",true,"Retry")
            }else if(playlist_description_edittext.text.toString().isNullOrEmpty()){
                ViewUtils.showSnackBar(this@CreatePlaylistActivity,"Please Enter Playlist Description",true,"Retry")
            }else{
                getApiData()
        }
        }
    }

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@CreatePlaylistActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            hashMap["name"] = playlist_name_edittext.text.toString()
            hashMap["desc"] = playlist_description_edittext.text.toString()
//            hashMap["playlist_id"] = intent.getStringExtra("id")
            createPlaylistViewModel.createPlaylist(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@CreatePlaylistActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }

    private fun getResponse() {
        createPlaylistViewModel.createPlaylistListLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), CreatePlaylistModel::class.java)
                if (response.meta.code == 210) {
                    snackbarwithbutton=Snackbar.make(findViewById(R.id.parent_layout), response.meta.message, Snackbar.LENGTH_LONG)
                    snackbarwithbutton!!.show();
                    finish()
                } else {
                    showErrorDialog(response.meta.message)
                }
            }
        })
    }
}