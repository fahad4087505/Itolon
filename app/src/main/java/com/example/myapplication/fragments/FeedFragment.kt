package com.example.myapplication.fragments

import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.colepower.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.adapters.FeedAdapter
import com.example.myapplication.adapters.RecyclerViewClickListener
import com.example.myapplication.adapters.SliderAdapter
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postmodel.Results
import com.example.myapplication.model.screenimagesmodel.ImageResult
import com.example.myapplication.model.screenimagesmodel.ScreenImages
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.FeedViewModel
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.action_bar_layout.view.*
import kotlinx.android.synthetic.main.fragment_feed_list.*
import kotlinx.android.synthetic.main.fragment_feed_list.view.*
import org.json.JSONObject
import java.io.File
import java.util.*

class FeedFragment : Fragment(), FeedLikeClickInterface, ClickInterface, RecyclerViewClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var items: MutableList<Results> = mutableListOf()
    var sliderImagesList: MutableList<ImageResult> = mutableListOf()
    private lateinit var feedViewModel: FeedViewModel
    val progressBar = CustomProgressBar()
    private var filePath = ""
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayerLength = 0
    private var duration: Int = 0
    private var mView:View?=null
    var  sliderImagesAdapter:SliderAdapter?=null
    var sliderView: SliderView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView= inflater.inflate(R.layout.fragment_feed_list, container, false)
        feedViewModel = ViewModelProvider.NewInstanceFactory().create(FeedViewModel::class.java)
        init(mView!!)
        return mView
    }
    private fun getApiData() {
        if (Utils.getInstance().isNetworkConnected(requireActivity())) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            feedViewModel.getFeeds(hashMap)
            getResponse()
        }
        else{
            showErrorDialog()
        }
    }
    private fun showErrorDialog(){
        ViewUtils.showSnackBar(requireContext(),"No Internet Connection",false,"Retry").subscribe{res->
            if(res){
                getApiData()
            }
        }
    }

    private fun init(view: View) {
        if(Utils.getInstance().isNetworkConnected(requireActivity())) {
            getApiData()
        }else{
            ViewUtils.showSnackBar(requireContext(),"No Internet Connection",false,"Retry").subscribe{res->
                if(res){
                    getApiData()
                }
            }
        }
        feedViewModel.getScreenImages()
        getImagesResponse()
        progressBar.show(activity as MainActivity)
        view.titleTextview?.text = getString(R.string.feed)
        manager = LinearLayoutManager(activity)
        view.feedRecyclerview?.adapter = FeedAdapter(items, (activity as MainActivity), this,this)
        view.feedRecyclerview?.layoutManager = manager
        view.swipeToRefresh?.setOnRefreshListener {
            view.swipeToRefresh.isRefreshing=false
        }
        view.feedRecyclerview?.adapter?.notifyDataSetChanged()
        pagination(view)
        setSliderImages(sliderImagesList)
    }

    private fun setSliderImages(imagesList:List<ImageResult>){
        sliderView = mView!!.findViewById(R.id.imageSlider)
        sliderImagesAdapter = SliderAdapter(activity,imagesList)
        sliderView!!.setSliderAdapter(sliderImagesAdapter!!)
        sliderView!!.setIndicatorAnimation(IndicatorAnimations.THIN_WORM) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView!!.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView!!.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        sliderView!!.indicatorSelectedColor = Color.WHITE
        sliderView!!.indicatorUnselectedColor = Color.GRAY
        sliderView!!.scrollTimeInSec = 3
        sliderView!!.isAutoCycle = true
        sliderView!!.startAutoCycle()
        sliderView!!.setOnIndicatorClickListener(object : DrawController.ClickListener {
           override fun onIndicatorClicked(position: Int) {
               sliderView!!.currentPagePosition = position
            }
        })
    }
    private fun pagination(view: View) {
        view.feedRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun showDialog(check: Boolean?, audioUrl: String?, trackId: String?, position: Int) {
    }

    override fun click(url: String, imageView: ImageView, textView: TextView) {
    }

    override fun previousButtonClick(position: Int) {

    }

    override fun nextButtonClick(position: Int) {
    }

    override fun onStop() {
        super.onStop()
//        stopMediaPlayer()
        if(progressBar.dialog.isShowing) {
            progressBar.dialog.dismiss()
        }
    }

    override fun clickListener(id: String?, postId: String?, likeStatus: Int, shareUrl: String?, position: Int) {
    }
    private fun getResponse() {
        feedViewModel.postsLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Post::class.java)
                if (response.meta.code == 205) {
                    if(response.results.size>0) {
                        items.addAll(response.results)
                        setVisibilty(true)
                    }else{
                        setVisibilty(false)
                    }
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                }else{
                    setVisibilty(false)
//                    Toast.makeText(activity!!,response.meta.message, Toast.LENGTH_LONG).show()
                    ViewUtils.showSnackBar(requireContext(),response.meta.message,false,"Retry").subscribe{res->
                        if(res){
                            getApiData()
                        }
                    }
                }
            }else{
                ViewUtils.showSnackBar(requireContext(),"Server is not responding",false,"Retry").subscribe{res->
                    if(res){
                        getApiData()
                    }
                }
                setVisibilty(false)

            }
        })
    }
    private fun getImagesResponse() {
        feedViewModel.screenImagesLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), ScreenImages::class.java)
                if (response.meta.code == 210) {
                    sliderImagesList.clear()
                    sliderImagesList.addAll(listOf(response.result))
                    sliderImagesAdapter!!.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity!!,response.meta.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onClick(position: Int,imageView: ImageView,songUrl:String) {
        downloadFile(songUrl)
    }

    override fun stopMediaPlayer(position: Int) {
        stopMediaPlayer()
    }

    override fun previousButtonClick(position: Int, songUrl: String) {
        mView!!.feedRecyclerview.scrollToPosition(position)
        downloadFile(songUrl)
    }

    override fun nextButtonClick(position: Int, songUrl: String) {
        mView!!.feedRecyclerview.scrollToPosition(position)
        downloadFile(songUrl)
    }

    override fun randomButtonClick(position: Int, songUrl: String) {
        mView!!.feedRecyclerview.scrollToPosition(position)
        downloadFile(songUrl)
    }

    override fun onPause() {
        super.onPause()
        Utils.getInstance().killMediaPlayer(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.getInstance().killMediaPlayer(null)
    }


    private fun downloadFile(url: String) {
        progressBar.show(activity!!)
        FileLoader.with(activity).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object :
                FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
                    filePath = loadedFile.path
                    progressBar.dialog.dismiss()
                    stopMediaPlayer()
                    playMusic(filePath)
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    progressBar.dialog.dismiss()
                }
            })
    }
    private fun playMusic(filePath: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource(filePath)
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
        duration = mediaPlayer!!.duration
        mediaPlayer!!.setOnCompletionListener {
            mediaPlayer!!.stop()
        }
    }
    private fun stopMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                } catch (ignored: Exception) {
                }
            }
        } catch (ignored: Exception) {
        }
    }
    private fun setVisibilty(flag:Boolean){
        if(flag){
            no_data_textview.visibility=View.GONE
            swipeToRefresh.visibility=View.VISIBLE
        }else{
            no_data_textview.visibility=View.VISIBLE
            swipeToRefresh.visibility=View.GONE
        }
    }
}