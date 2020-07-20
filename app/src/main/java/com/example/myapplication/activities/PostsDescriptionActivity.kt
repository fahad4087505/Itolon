package com.example.myapplication.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_post_description.*

class PostsDescriptionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_description)
        back_arrow_imageview.setOnClickListener{
            finish()
        }
        if (intent.hasExtra("title")) {
            title_textview.text = intent.getStringExtra("title")
            titleTextview.text = intent.getStringExtra("title")
        }
        if (intent.hasExtra("description")) {
            title_textview.text = intent.getStringExtra("description")
        }
        if (intent.hasExtra("imageUrl")) {
            Glide.with(this@PostsDescriptionActivity).load(intent.getStringExtra("imageUrl")).placeholder(R.drawable.maxresdefault).error(R.drawable.maxresdefault).into(post_imageview)
        }
    }
}