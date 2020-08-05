package com.example.myapplication.activities

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.albumdetailmodel.Song
import com.example.myapplication.model.playlistdetailmodel.PlaylistResult
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult
import com.example.myapplication.utils.Utils
import kotlinx.android.synthetic.main.activity_teaser.*
import java.util.*
import java.util.concurrent.TimeUnit

class TeaserActivity : BaseActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var albumDetailModel: Song? = null
    private var playlistResult: PlaylistResult? = null
    private var albumDetailModelDownloads: UserDownloadResult? = null
    private var albumDetailModelSong: com.example.myapplication.model.artistdetailmodel.Song? = null
    private var isPlayFlag = false
    private var timeWhenStopped: Long = 0
    private var mediaPlayerLength = 0
    private var duration = ""
    private var mediaPlayerDuration = 0
    private var position = 0
    private var completeFlag = false
    private var firstTimePlay = false
    var seconds:Long=0
    var elapsedMillis:Long=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teaser)
        back_arrow_imageview.setOnClickListener {
            finish()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startPostponedEnterTransition()
            }
        }
        try {
            position = intent.getIntExtra("position", 0)
            if (intent.hasExtra("filePath")) {
                playMusic(intent.getStringExtra("filePath"))
            }
            if (Constants.songsArrayList.size > 0) {
                playMusic(Constants.songsArrayList[intent.getIntExtra("position", 0)])
            }
            if (intent.hasExtra("currentItem")) {
                albumDetailModel = intent.getSerializableExtra("currentItem") as Song
                track_name_textview.text = albumDetailModel!!.name
                artist_name_textview.text = albumDetailModel!!.artistName
            }
            if (intent.hasExtra("currentPlaylistItemTrack")) {
                playlistResult = intent.getSerializableExtra("currentPlaylistItemTrack") as PlaylistResult
                track_name_textview.text = playlistResult!!.songs[position].name
//                artist_name_textview.text = playlistResult!!.songs[position].
            }
            if (intent.hasExtra("currentItemDownloaded")) {
                albumDetailModelDownloads = intent.getSerializableExtra("currentItemDownloaded") as UserDownloadResult
                if (!albumDetailModelDownloads!!.name.isNullOrEmpty()) {
                    track_name_textview.text = albumDetailModelDownloads!!.name
                }
                if (!albumDetailModelDownloads!!.artistName.isNullOrEmpty()) {
                    artist_name_textview.text = albumDetailModelDownloads!!.artistName
                }
            }
            if (intent.hasExtra("currentItemTrack")) {
                albumDetailModelSong = intent.getSerializableExtra("currentItemTrack") as com.example.myapplication.model.artistdetailmodel.Song
                if (!albumDetailModelSong!!.name.isNullOrEmpty()) {
                    track_name_textview.text = albumDetailModelSong!!.name
                }
                if (!albumDetailModelSong!!.artistName.isNullOrEmpty()) {
                    artist_name_textview.text = albumDetailModelSong!!.artistName
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        play_song_imageview.setOnClickListener {
            playSong()
        }
        shuffle_music_play.setOnClickListener {
            playRandom()
        }
        previous_song_play_icon.setOnClickListener {
            if (position > 0) {
            try {
                if(mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                    mediaPlayer=null
                }
                isPlayFlag=false
                completeFlag = false
                firstTimePlay=false
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.stop()
                timeWhenStopped=0
                seconds=0
                elapsedMillis=0
                seekBar.progress = 0
                playMusic(Constants.songsArrayList[position - 1])
                playSong()
                position -= 1
            } catch (e: Exception) {
                e.printStackTrace()
            }
            }
        }
        next_song_play_icon.setOnClickListener {
            try {
                if (position >= 0 && Constants.songsArrayList[position + 1] != null) {
                    try {
                        if(mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.stop()
                            mediaPlayer=null
                        }
                        isPlayFlag=false
                        completeFlag = false
                        firstTimePlay=false
                        chronometer.base = SystemClock.elapsedRealtime()
                        chronometer.stop()
                        timeWhenStopped=0
                        seconds=0
                        elapsedMillis=0
                        seekBar.progress = 0
                        playMusic(Constants.songsArrayList[position - 1])
                        playSong()
                        position += 1
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
            }
        }

        refresh_music_player.setOnClickListener {
            refreshSong()
        }
    }
    private fun refreshSong(){
        try {
            if (position >= 0 ) {
                try {
                    if(mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.stop()
                        mediaPlayer=null
                    }
                    isPlayFlag=false
                    completeFlag = false
                    firstTimePlay=false
                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.stop()
                    timeWhenStopped=0
                    seconds=0
                    elapsedMillis=0
                    seekBar.progress = 0
                    playMusic(Constants.songsArrayList[position])
                    playSong()
//                        position += 1
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playSong(){
        if(mediaPlayer==null){
           playMusic(Constants.songsArrayList[position])
        }
        if (!isPlayFlag) {
            isPlayFlag = true
            Glide.with(this).load(R.drawable.ic_baseline_pause_24).into(play_icon_imageview)
//            play_icon_imageview.background = ContextCompat.getDrawable(this@TeaserActivity, R.drawable.ic_baseline_pause_24)
            if (mediaPlayer != null && !firstTimePlay) {
                firstTimePlay = true
                startTime()
            }
            chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
            chronometer.start()
            if (mediaPlayerLength != 0) {
                mediaPlayer?.seekTo(mediaPlayerLength)
            }
            mediaPlayer?.start()
        } else {
            isPlayFlag = false
            Glide.with(this).load(R.drawable.ic_baseline_play_arrow_24).into(play_icon_imageview)
//            play_icon_imageview.background = ContextCompat.getDrawable(this@TeaserActivity, R.drawable.ic_baseline_play_arrow_24)
            timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
            chronometer.stop()
            mediaPlayer?.pause()
            mediaPlayerLength = mediaPlayer?.currentPosition!!
            if (mediaPlayerLength != 0) {
                mediaPlayer?.seekTo(mediaPlayerLength)
            }
            if (mediaPlayer != null) {
                mediaPlayer!!.pause()
            }
        }
    }
    private fun playMusic(filePath: String) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            }
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setDataSource(filePath)
            mediaPlayer!!.prepare()
            mediaPlayerDuration = (mediaPlayer!!.duration / 1000)
            duration = Utils.getInstance().getDurationInMinutes(mediaPlayer!!.duration.toLong())
            total_duration_textview.text = duration
            seekBar.max = mediaPlayerDuration
            duration_textview.text = duration
            mediaPlayer!!.setOnCompletionListener {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
//        stopMediaPlayer()
    }

    private fun startTime() {
        chronometer.setOnChronometerTickListener {
            try {
                elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
                seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)
                if (seconds.toInt() == mediaPlayerDuration) {
                    mediaPlayer = null
                    play_icon_imageview.background = ContextCompat.getDrawable(this@TeaserActivity, R.drawable.ic_baseline_play_arrow_24)
                    isPlayFlag = false
                    completeFlag = false
                    firstTimePlay = false
                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.stop()
                    timeWhenStopped = 0
                    seconds = 0
                    elapsedMillis = 0
                    seekBar.progress = 0
                }
                seekBar.progress = seconds.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        seekBar.isEnabled = false
    }

    private fun playRandom(){
       val random = Random()
       val songIndex = random.nextInt(Constants.songsArrayList.size)
       if (songIndex >= 0 && Constants.songsArrayList.size>1) {
            try {
                if(mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                    mediaPlayer=null
                }
                isPlayFlag=false
                completeFlag = false
                firstTimePlay=false
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.stop()
                timeWhenStopped=0
                seconds=0
                elapsedMillis=0
                seekBar.progress = 0
                playMusic(Constants.songsArrayList[songIndex])
                playSong()
                position =songIndex
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
           refreshSong()
       }
    }

    override fun onStop() {
        super.onStop()
//        stopMediaPlayer()
    }


    private fun stopMediaPlayer(){
        try {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }
}