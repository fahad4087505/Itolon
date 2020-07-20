package com.example.myapplication.activities
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.ArtistAdapter
import com.example.myapplication.adapters.PlaylistAdapter
import com.example.myapplication.adapters.PlaylistItemClickListener
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.allartistmodel.AllArtistModel
import com.example.myapplication.model.playlistdetailmodel.PlaylistDetail
import com.example.myapplication.model.playlistdetailmodel.PlaylistResult
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.viewmodel.AllArtistsViewModel
import com.example.myapplication.viewmodel.PlaylistDetailViewModel
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_playlists.*
import kotlinx.android.synthetic.main.activity_playlists.artists_recyclerview
import kotlinx.android.synthetic.main.activity_playlists.back_arrow_imageview
import kotlinx.android.synthetic.main.activity_playlists.playlist_recyclerview
import kotlinx.android.synthetic.main.activity_playlists.profile_image
import kotlinx.android.synthetic.main.activity_playlists.tracks_count
import kotlinx.android.synthetic.main.activity_teaser.*
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class PlaylistActivity : BaseActivity(), PlaylistItemClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var clickPosition=-1
    private var duration=""
    private lateinit var playlistDetailViewModel: PlaylistDetailViewModel
    private var mediaPlayer: MediaPlayer? = null
    val songsArrayList=ArrayList<String>()
    val videoArrayList=ArrayList<String>()

    var items: MutableList<PlaylistResult> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistDetailViewModel = ViewModelProvider.NewInstanceFactory().create(PlaylistDetailViewModel::class.java)
        setContentView(R.layout.activity_playlists)
        init()
    }
    private fun init(){
        if(intent.hasExtra("title")) {
            playlist_titleTextview.text = intent.getStringExtra("title")
        }
        val hashMap = HashMap<String, String>()
        hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
        hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
        hashMap["playlist_id"] = intent.getStringExtra("id")
        playlistDetailViewModel.getPlaylistDetail(hashMap)
        getResponse()
        progressBar.show(this)
        manager = LinearLayoutManager(this)
        playlist_recyclerview.adapter = PlaylistAdapter(items,this, this,this)
        playlist_recyclerview.layoutManager = manager
        pagination()
        back_arrow_imageview.setOnClickListener {
            finish()
        }
        profile_image.setOnClickListener {
            startActivity(Intent(this@PlaylistActivity, ArtistsActivity::class.java))
        }
        setArtistsAdapter()
    }

    private fun setArtistsAdapter(){
        val layoutManager = LinearLayoutManager(this@PlaylistActivity,LinearLayoutManager.HORIZONTAL,false)
        artists_recyclerview.layoutManager = layoutManager
//        artists_recyclerview.adapter = ArtistAdapter(items, this, this)
    }

    private fun pagination() {
        playlist_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager!!.childCount
                totalItems = manager!!.itemCount
                scrollOutItems = manager!!.findFirstVisibleItemPosition()
                if (isScrolling == true && currentItems + scrollOutItems == totalItems && dy > 0) {
                    isScrolling = false
                    count += 1
                }
            }
        })
    }
    private fun getResponse() {
        playlistDetailViewModel.playlistDetailLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), PlaylistDetail::class.java)
                if (response.meta.code == 205) {
                    tracks_count.text=response.result.size.toString()+" Tracks"
                    if(response.result.size>0) {
                        trackVisibility(true)
                        items.addAll(response.result)
                        for(i in 0 until response.result.size){
                            downloadFile("http://44.231.47.188"+response.result[i].content.filePath)
                            items[i].duration=getDuration("http://44.231.47.188"+response.result[i].content.filePath)
                        }
                        playlist_recyclerview.adapter!!.notifyDataSetChanged()
                    }else{
                        trackVisibility(false)
                    }
                } else if(response.meta.code==404) {
                    trackVisibility(false)
                }else {
                    trackVisibility(false)
                }
            }else{
                trackVisibility(false)
            }
        })
    }

    override fun onClick(position: Int, songUrl: String) {
        clickPosition=position
//        downloadFile(songUrl)
    }
    private fun downloadFile(url: String) {
        progressBar.show(this)
        FileLoader.with(this).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
//                    filePath = loadedFile.path
                    if(loadedFile.path.contains(".mp3")) {
                        songsArrayList.add(loadedFile.path)
//                        startActivity(Intent(this@PlaylistActivity, TeaserActivity::class.java).putExtra("filePath", loadedFile.path).putExtra("currentPlaylistItemTrack", items[clickPosition]))
                    }else{
                        videoArrayList.add(loadedFile.path)
//                        startActivity(Intent(this@PlaylistActivity, VideoPlayActivity::class.java).putExtra("filePath",loadedFile.path))
//                        Toast.makeText(this@PlaylistActivity,"File not supported", Toast.LENGTH_LONG).show()
                    }
                    clickPosition=-1
                    progressBar.dialog.dismiss()
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    progressBar.dialog.dismiss()
//                    startActivity(
//                        Intent(
//                            this@PlaylistActivity,
//                            VideoPlayActivity::class.java
//                        ))
//                    Toast.makeText(this@ArtistsActivity,"File format not supported",Toast.LENGTH_LONG).show()
                }
            })
    }
    private fun trackVisibility(flag:Boolean){
        if(flag){
            track_scrollview.visibility= View.VISIBLE
            no_result_textview.visibility= View.GONE
        }else{
            track_scrollview.visibility= View.GONE
            no_result_textview.visibility= View.VISIBLE
        }
    }

    private fun getDuration(filePath: String):String {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource(filePath)
        mediaPlayer!!.prepare()
        duration = Utils.getInstance().getDurationInMinutes(mediaPlayer!!.duration.toLong())
        return duration
    }
}