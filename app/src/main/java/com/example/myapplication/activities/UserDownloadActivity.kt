package com.example.myapplication.activities

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.*
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.userdownloadsmodel.UserDownloadModel
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.utils.ViewUtils.showSnackBar
import com.example.myapplication.viewmodel.UserDownloadsViewModel
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.action_bar_layout.*
import kotlinx.android.synthetic.main.fragment_search_track.*
import kotlinx.android.synthetic.main.fragment_search_track.feedRecyclerview
import kotlinx.android.synthetic.main.fragment_search_track.search_imageview
import kotlinx.android.synthetic.main.fragment_search_track.swipeToRefresh
import kotlinx.android.synthetic.main.fragment_search_track.title_textview
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class UserDownloadActivity : BaseActivity(),  DownloadSongClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: GridLayoutManager? = null
    var count: Int = 6
    var clickPosition=-1
    var items: MutableList<UserDownloadResult> = mutableListOf()
    var searchItem: MutableList<UserDownloadResult> = mutableListOf()
    private var mediaPlayer: MediaPlayer? = null
    private var duration=""
    var playFilePath=""
    var playSongPosition=-1
    private lateinit var userDownloadsViewModel: UserDownloadsViewModel
    val songsArrayList=ArrayList<String>()
    val videoArrayList=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDownloadsViewModel = ViewModelProvider.NewInstanceFactory().create(UserDownloadsViewModel::class.java)
        setContentView(R.layout.fragment_search_track)
        init()
    }

    private fun init() {
        titleTextview?.text ="Search Track"
        if(intent.hasExtra("title")){
            title_textview.text=intent.getStringExtra("title")
        }
        setAdapter(items)
        search_data_edittext.addTextChangedListener(object : TextWatcher {
            override   fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().isNullOrEmpty()){
                    searchValue(s.toString().toLowerCase())
                }
                else{
                    setAdapter(items)
                }
            }

            override   fun afterTextChanged(editable: Editable?) {}
        })
        swipeToRefresh?.setOnRefreshListener {
        swipeToRefresh.isRefreshing=false
        }
        back_imageview.setOnClickListener {
            finish()
        }
       search_imageview.setOnClickListener {
            startActivity(Intent(this@UserDownloadActivity,SearchActivity::class.java))
        }
        feedRecyclerview?.adapter?.notifyDataSetChanged()
        pagination()
        getApiData()

    }

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@UserDownloadActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            userDownloadsViewModel.getUserDownloads(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
       showSnackBar(this@UserDownloadActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }

    private fun searchValue(value: String) {
        searchItem.clear()
        for (i in 0 until items.size) {
            if (items[i].name.toLowerCase().contains(value.toLowerCase())) {
                searchItem.add(items[i])
            }
        }
        setAdapter(searchItem)
    }

    private fun pagination() {
        feedRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun setAdapter(itemsArray:MutableList<UserDownloadResult>){
        manager = GridLayoutManager(this@UserDownloadActivity,3)
        feedRecyclerview?.adapter = UserDownloadAdapter(itemsArray, (this@UserDownloadActivity), this,this)
        feedRecyclerview?.layoutManager = manager
    }
    private fun getResponse() {
        userDownloadsViewModel.userDownloadLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), UserDownloadModel::class.java)
                if (response.meta.code == 205) {
                    if(response.result!=null&&response.result.size>0) {
                        trackVisibility(true)
                        items.addAll(response.result)
                        for(i in 0 until response.result.size){
                            downloadFile("http://44.231.47.188"+response.result[i].content.filePath)
                            items[i].duration=getDuration("http://44.231.47.188"+response.result[i].content.filePath)
                        }
                    }else{
                        trackVisibility(false)
                    }
                    progressBar.dialog.dismiss()
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                } else {
                    trackVisibility(false)
                    progressBar.dialog.dismiss()
                    showErrorDialog(response.meta.message)
                }

            }else{
                if(progressBar.dialog.isShowing) {
                    progressBar.dialog.dismiss()
                }
                trackVisibility(false)
                showErrorDialog("Server is not responding")
            }
        })
    }
    private fun downloadFile(url: String) {
        if(!progressBar.dialog.isShowing) {
            progressBar.show(this)
        }
        FileLoader.with(this).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("Itolon", FileLoader.DIR_EXTERNAL_PUBLIC).asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
//                    filePath = loadedFile.path
                    if(loadedFile.path.contains(".mp3")) {
                        songsArrayList.add(loadedFile.path)
//                        startActivity(Intent(this@PlaylistActivity, TeaserActivity::class.java).putExtra("filePath", loadedFile.path).putExtra("currentPlaylistItemTrack", items[clickPosition]))
                    }else{
                        videoArrayList.add(loadedFile.path)
                    }
                    clickPosition=-1
                    if(progressBar.dialog.isShowing) {
                        progressBar.dialog.dismiss()
                    }
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    if(progressBar.dialog.isShowing) {
                        progressBar.dialog.dismiss()
                    }
                }
            })
    }
//    private fun downloadFile(url: String) {
//        progressBar.show(this)
//        FileLoader.with(this).load(url, false) //2nd parameter is optioal, pass true to force load from network
//            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object :
//                FileRequestListener<File> {
//                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
//                    val loadedFile = response.body
//                    progressBar.dialog.dismiss()
//                    if(loadedFile.path. contains(".mp3")) {
//                        startActivity(Intent(this@UserDownloadActivity, TeaserActivity::class.java).putExtra("filePath", loadedFile.path).putExtra("currentItemDownloaded", items.get(clickPosition)))
//                    }else{
//                        startActivity(Intent(this@UserDownloadActivity, VideoPlayActivity::class.java).putExtra("filePath",loadedFile.path))
//                    }
//                    clickPosition=-1
//                }
//                override fun onError(request: FileLoadRequest, t: Throwable) {
//                    progressBar.dialog.dismiss()
//                    showSnackBar(this@UserDownloadActivity,"File format not supported",true,"")
//                }
//            })
//    }
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
    override fun onClick(position: Int, songUrl: String) {
        clickPosition=position
        if(songUrl.contains(".mp3"))
        {
            Constants.songsArrayList.clear()
            Constants.songsArrayList.addAll(songsArrayList)
            playSongPosition=computePosition(songUrl)
            if(playSongPosition!=-1) {
                startActivity(Intent(this@UserDownloadActivity, TeaserActivity::class.java).putExtra("position", playSongPosition).putExtra("currentPlaylistItemTrack", items[clickPosition]))
                playSongPosition = -1
            }else{
                showSnackBar("File is not exist",false)
            }
        }else{
            Constants.songsArrayList.clear()
            Constants.songsArrayList.addAll(videoArrayList)
            playSongPosition=computePosition(songUrl)
            if(playSongPosition!=-1) {
                startActivity(Intent(this@UserDownloadActivity, VideoPlayActivity::class.java).putExtra("position", playSongPosition))
            }else{
                showSnackBar("File is not exist",false)
            }
        }
//        downloadFile(songUrl)
    }

    private fun trackVisibility(flag:Boolean){
        if(flag){
            swipeToRefresh.visibility= View.VISIBLE
            no_result_textview.visibility= View.GONE
        }else{
            swipeToRefresh.visibility= View.GONE
            no_result_textview.visibility= View.VISIBLE
        }
    }
    private fun computePosition(songUrl: String):Int{
        val fileName=Utils.getInstance().getLastString(songUrl)
        for(i in 0 until Constants.songsArrayList.size){
            if(Constants.songsArrayList.get(i).contains(fileName)){
                playSongPosition=i
                playFilePath= Constants.songsArrayList.get(i)
            }
        }
        return playSongPosition
    }
}