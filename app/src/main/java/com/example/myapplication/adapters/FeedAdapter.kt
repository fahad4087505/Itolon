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
import com.example.myapplication.utils.Utils
import kotlinx.android.synthetic.main.cell_feed_list.view.*

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
                        clickPosition = position
                        clickListener.onClick(position,holder.playIcon,"http://44.231.47.188"+items[position].content.filePath)
                        clickPosition=position
                        notifyDataSetChanged()
                    }
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
}
interface RecyclerViewClickListener {
    fun onClick( position: Int,imageView: ImageView,songUrl:String)
    fun stopMediaPlayer( position: Int)
}
class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val playIcon:ImageView=view.play_icon
    val mPostImageView:ImageView=view.backgroundImageView
}