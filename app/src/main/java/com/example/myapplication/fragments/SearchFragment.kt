package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.activities.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        view.stars_layout.setOnClickListener {
            startActivity(Intent(activity, StarsActivity::class.java).putExtra("title","Stars"))
        }
        view.mes_layout.setOnClickListener {
            startActivity(Intent(activity, UserDownloadActivity::class.java).putExtra("title","Mes Telechargements"))
        }
        view.artist_layout.setOnClickListener {
            startActivity(Intent(activity, AllArtistsActivity::class.java).putExtra("title","Artists"))
        }
        view.albems_layout.setOnClickListener {
            startActivity(Intent(activity, AlbumActivity::class.java).putExtra("title","Albems"))
        }
        view.playlist_layout.setOnClickListener {
            startActivity(Intent(activity, PlaylistsActivity::class.java).putExtra("title","Playlist"))
        }
    }
}