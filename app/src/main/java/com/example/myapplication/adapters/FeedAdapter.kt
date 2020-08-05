package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.postmodel.Results
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.utils.Utils
import kotlinx.android.synthetic.main.cell_feed_list.view.*
import java.util.*

class FeedAdapter(val items: List<Results>, val context: Context, val fragment: FeedFragment, var  clickListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<FeedViewHolder>() {
    private var clickPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_feed_list, parent, false))
    }
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
            try {
                this.mClickInterface = fragment
                this.mPostDetails = fragment
                Glide.with(context).load("http://44.231.47.188"+items[position].imagePath).error(R.drawable.app_image).error(R.drawable.app_image).into(holder.mPostImageView)
                holder.playIcon.setOnClickListener {
                    if (oldPosition != -1 && oldPosition == position) {
                        oldPosition = -1
                        clickPosition = -1
                        holder.playIcon.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp)
                        Utils.getInstance().killMediaPlayer(null)
                        clickListener.stopMediaPlayer(position)
                    } else {
                        if(items[position].content!=null) {
                            clickListener.onClick(position, holder.playIcon, "http://44.231.47.188" + items[position].content.filePath)
                            clickPosition = position
                            notifyDataSetChanged()
                        }
                    }
                }

                holder.nextSongPlayIcon.setOnClickListener {
                    if (position<items.size&&items[position + 1].content!=null) {
                        clickPosition = position+1
                        notifyDataSetChanged()
                        clickListener.nextButtonClick(position + 1, "http://44.231.47.188" + items[position + 1].content.filePath)
                    }
                }

                holder.previousSongPlayIcon.setOnClickListener {
                    if(position>0&&position<items.size&&items[position - 1].content!=null){
                        clickPosition = position-1
                        notifyDataSetChanged()
                    clickListener.previousButtonClick(position-1,"http://44.231.47.188"+items[position-1].content.filePath)
                }
                }
                holder.replaySongPlayIcon.setOnClickListener {
                    if(items[position].content!=null) {
                        clickListener.onClick(position, holder.playIcon, "http://44.231.47.188" + items[position].content.filePath)
                        clickPosition = position
                        notifyDataSetChanged()
                    }
                }
                holder.randomSongPlayIcon.setOnClickListener {
                    playRandom(holder)
                }

                if(clickPosition==position){
                    oldPosition = clickPosition
                    holder.playIcon.setBackgroundResource(R.drawable.ic_pause)
                }else{
                    holder.playIcon.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp)
                }
            }
            catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
    private fun playRandom(holder: FeedViewHolder){
        val random = Random()
        val songIndex = random.nextInt(items.size)
        if (songIndex >= 0 && items.size>1) {
            try {
                if(items[songIndex].content!=null) {
                    clickListener.randomButtonClick(songIndex, "http://44.231.47.188" + items[songIndex].content.filePath)
                    clickPosition = songIndex
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
//            refreshSong()
        }
    }
}
interface RecyclerViewClickListener {
    fun onClick( position: Int,imageView: ImageView,songUrl:String)
    fun stopMediaPlayer( position: Int)
    fun previousButtonClick(position: Int,songUrl:String)
    fun nextButtonClick(position: Int,songUrl:String)
    fun randomButtonClick(position: Int,songUrl:String)
}
class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val playIcon:ImageView=view.play_icon
    val nextSongPlayIcon:ImageView=view.next_song_play_icon
    val previousSongPlayIcon:ImageView=view.previous_song_play_icon
    val replaySongPlayIcon:ImageView=view.replay_icon_imageview
    val randomSongPlayIcon:ImageView=view.random_icon_imageview
    val mPostImageView:ImageView=view.backgroundImageView
}
