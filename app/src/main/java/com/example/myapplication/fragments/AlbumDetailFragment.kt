package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.colepower.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.activities.AlbumsActivity
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.activities.TeaserActivity
import com.example.myapplication.activities.VideoPlayActivity
import com.example.myapplication.adapters.AlbumDetailAdapter
import com.example.myapplication.adapters.CategoriesSongAdapter
import com.example.myapplication.adapters.CategorySongClickListener
import com.example.myapplication.adapters.UserDownloadAdapter
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.categoriessongmodel.Song
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult
import com.example.myapplication.utils.ViewUtils.showSnackBar
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.fragment_album_detail.*
import kotlinx.android.synthetic.main.fragment_album_detail.view.*
import kotlinx.android.synthetic.main.fragment_album_detail.view.feedRecyclerview
import kotlinx.android.synthetic.main.fragment_album_detail.view.no_result_textview
import kotlinx.android.synthetic.main.fragment_album_detail.view.swipeToRefresh
import java.io.File

class AlbumDetailFragment(val songsList:ArrayList<Song>) : Fragment(),
    CategorySongClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: GridLayoutManager? = null
    var count: Int = 6
    var items: MutableList<String> = mutableListOf()
    var mImageView: ImageView? = null
    var mView:View?=null
    val progressBar = CustomProgressBar()
    var clickPosition=-1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_album_detail, container, false)
        mView=view
        init(view)
        return view
    }

    private fun init(view: View) {
        Log.e("songsArrary",songsList.size.toString())
//        if(songsList.size>0) {
            setAdapter(songsList)
//            setVisibilty(true)
//        }else{
//            setVisibilty(false)
//        }
//        manager = GridLayoutManager(activity,2)
//        view.feedRecyclerview?.adapter = AlbumDetailAdapter(items, (activity as AlbumsActivity), this)
//        view.feedRecyclerview?.layoutManager = manager
        view.swipeToRefresh?.setOnRefreshListener {
            view.swipeToRefresh.isRefreshing=false
        }
        try {
            if(songsList.size>0) {
//                setAdapter(songsList)
                setVisibilty(true)
            }else{
                setVisibilty(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (i in 0 until 6) {
            items.add("")
        }
//        view.feedRecyclerview?.adapter?.notifyDataSetChanged()
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
    private fun setAdapter(itemsArray:MutableList<Song>){
        manager = GridLayoutManager(requireContext(),3)
        mView!!.feedRecyclerview?.adapter = CategoriesSongAdapter(itemsArray, (requireActivity()),this, this)
        mView!!.feedRecyclerview?.layoutManager = manager

    }


    override fun onStop() {
        super.onStop()
    }

    override fun onClick(position: Int, songUrl: String) {
        clickPosition=position
        downloadFile(songUrl)

    }
    private fun downloadFile(url: String) {
        progressBar.show(requireActivity())
        FileLoader.with(requireActivity()).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object :
                FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
                    progressBar.dialog.dismiss()
                    if(loadedFile.path. contains(".mp3")) {
                        startActivity(Intent(requireActivity(), TeaserActivity::class.java).putExtra("filePath", loadedFile.path).putExtra("currentCategoryItemDownloaded", items.get(clickPosition)))
                    }else{
                        startActivity(Intent(requireActivity(), VideoPlayActivity::class.java).putExtra("filePath",loadedFile.path))
                    }
//                    clickPosition=-1
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    progressBar.dialog.dismiss()
                    showSnackBar(requireActivity(),"File format not supported",true,"")
                }
            })
    }
    private fun setVisibilty(flag:Boolean){
        if(flag){
            mView!!.no_result_textview.visibility=View.GONE
            mView!!.swipeToRefresh.visibility=View.VISIBLE
        }else{
            mView!!. no_result_textview.visibility=View.VISIBLE
            mView!!.swipeToRefresh.visibility=View.GONE
        }
    }
}