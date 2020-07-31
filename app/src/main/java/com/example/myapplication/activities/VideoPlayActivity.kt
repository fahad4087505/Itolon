package com.example.myapplication.activities

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.MediaController
import com.example.myapplication.R
import com.example.myapplication.prefrences.Constants
import kotlinx.android.synthetic.main.activity_video_play.*


class VideoPlayActivity : BaseActivity() {
    var isPurchased=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        if(intent.hasExtra("position")) {
            playMusic(Constants.songsArrayList.get(intent.getIntExtra("position",0)))
        }

        if(intent.hasExtra("is_purchased")) {
            isPurchased=  intent.getBooleanExtra("is_purchased",false)
        }
        if (intent.hasExtra("filePath")) {
            if (isPurchased) {
                playMusic(intent.getStringExtra("filePath"))
            } else {
                playSoundForXSeconds(intent.getStringExtra("filePath"), 30)
            }
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
    private fun playSoundForXSeconds(path:String, seconds: Int) {
        val soundUri: Uri = Uri.parse(path)
        video_view.setVideoURI(soundUri)
        if (soundUri != null) {
            val mp = MediaPlayer()
            try {
                mp.setDataSource(this@VideoPlayActivity, soundUri)
                mp.prepare()
                mp.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val mHandler = Handler()
            mHandler.postDelayed(Runnable {
                try {
                    mp.stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, (seconds * 1000).toLong())
        }
    }
}