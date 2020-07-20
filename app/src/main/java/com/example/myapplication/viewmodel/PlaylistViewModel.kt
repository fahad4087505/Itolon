package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.playlistmodel.PlaylistModel
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class PlaylistViewModel : ViewModel() {
    var playlistLiveData: LiveData<ApiResponse<PlaylistModel>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getPlaylists(hashMap:HashMap<String,String>) {
        playlistLiveData=retrofitClient.getPlaylists(hashMap)
    }
}