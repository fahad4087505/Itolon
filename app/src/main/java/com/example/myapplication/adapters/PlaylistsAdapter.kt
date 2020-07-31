package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.*
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albummodel.Result
import com.example.myapplication.model.allartistmodel.AllArtistResult
import com.example.myapplication.model.playlistmodel.PlaylistResult
import com.example.myapplication.model.starsmodel.StarResult
import kotlinx.android.synthetic.main.cell_albums.view.*

class PlaylistsAdapter(val items: List<PlaylistResult>, val context: Context, val fragment: PlaylistsActivity,val playListClickListener: PlayListClickListener) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_albums, parent, false))
    }
    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
            try {
                this.mClickInterface = fragment
                this.mPostDetails = fragment
                holder.mAlbumTitleTextView.text=items[position].name
                holder.cellLayout.setOnClickListener {
                    playListClickListener.onClick(position,items[position].playlistId)
                }
            } catch (e: Exception) {
                    e.printStackTrace()
            }
//
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface PlayListClickListener {
    fun onClick( position: Int,id:Int)
}
class PlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cellLayout:RelativeLayout=view.cell_layout
    val mAlbumTitleTextView:TextView=view.album_title
}