package com.example.myapplication.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.R
import com.example.myapplication.model.addcommentsmodel.AddCommentModel
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.PostsViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_post_description.*
import kotlinx.android.synthetic.main.activity_post_description.back_arrow_imageview
import kotlinx.android.synthetic.main.activity_post_description.titleTextview
import kotlinx.android.synthetic.main.activity_posts.*
import org.json.JSONObject
import java.util.HashMap

class PostsDescriptionActivity : BaseActivity() {
    private lateinit var postsViewModel: PostsViewModel
    private var postId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postsViewModel = ViewModelProvider.NewInstanceFactory().create(PostsViewModel::class.java)
        setContentView(R.layout.activity_post_description)
        back_arrow_imageview.setOnClickListener{
            finish()
        }
        add_comment_button.setOnClickListener{
            addComments()
        }
        if (intent.hasExtra("title")) {
            title_textview.text = intent.getStringExtra("title")
            titleTextview.text = intent.getStringExtra("title")
        }
        if (intent.hasExtra("description")) {
            title_textview.text = intent.getStringExtra("description")
        }
        if (intent.hasExtra("postId")) {
            postId = intent.getStringExtra("postId")
        }
        if (intent.hasExtra("imageUrl")) {
            Glide.with(this@PostsDescriptionActivity).load(intent.getStringExtra("imageUrl")).placeholder(R.drawable.maxresdefault).error(R.drawable.maxresdefault).into(post_imageview)
        }
    }
    private fun addComments(){
        if (Utils.getInstance().isNetworkConnected(this@PostsDescriptionActivity)) {
            if(comments_edittext.text.toString().isNullOrEmpty()){
                showErrorDialog("Please add comments")
            }else{
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            hashMap["post_id"] =postId
            hashMap["body"] = comments_edittext.text.toString()
            postsViewModel.getPosts(hashMap)
            getResponse()
            progressBar.show(this)
        }
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@PostsDescriptionActivity,message,true,"Retry")
    }
    private fun getResponse() {
        postsViewModel.postsLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), AddCommentModel::class.java)
                if (response.meta.code == 210) {
                } else {
                    showErrorDialog(response.meta.message)
                }
            }else{
                showErrorDialog("Server is not responding")
            }
        })
    }
}