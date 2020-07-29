package com.example.myapplication.activities

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_video_play.*


class VideoPlayActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        if(intent.hasExtra("position")) {
            playMusic(Constants.songsArrayList.get(intent.getIntExtra("filePath",0)))
        }
        if(intent.hasExtra("filePath")) {
            playMusic(intent.getStringExtra("filePath"))
        }
        back_arrow_imageview.setOnClickListener{
            finish()
        }
    }

    private fun playMusic(path:String) {
        progressBar.show(this@VideoPlayActivity)
        val uri: Uri = Uri.parse(path)
        video_view.setVideoURI(uri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(video_view)
        video_view.setMediaController(mediaController)
        video_view.start()
        video_view.setOnPreparedListener { mp ->
            progressBar.dialog.dismiss()
        }
    }
}