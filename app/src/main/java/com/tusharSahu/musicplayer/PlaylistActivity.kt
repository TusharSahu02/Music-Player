package com.tusharSahu.musicplayer

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tusharSahu.musicplayer.databinding.ActivityPlaylistBinding
import com.tusharSahu.musicplayer.databinding.AddPlaylistDialogBinding
import java.text.SimpleDateFormat
import java.util.*

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter :PlaylistViewAdapter

    companion object{
        var musicPlaylist : MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this@PlaylistActivity,3)
        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter


        binding.backBtnPlaylist.setOnClickListener {
            finish()
        }

        binding.addPlaylistBtn.setOnClickListener {
            customAlertDialog()
        }
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog,binding.root,false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)

        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add"){dialog, _ ->
                val playlistName = binder.playListName.text
                val createdBy = binder.yourName.text
                if(playlistName != null && createdBy != null){
                    if(playlistName.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlayList(playlistName.toString(),createdBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private  fun addPlayList(name:String,createdBy :String){
        var playlistExists = false

        for(i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playlistExists = true
                break
            }
        }

        if(playlistExists){
            Toast.makeText(this,"Playlist Exists",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Playlist Created ",Toast.LENGTH_SHORT).show()
            val tempPlaylist = Platlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calender)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }
}