package com.tusharSahu.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tusharSahu.musicplayer.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding :ActivityPlaylistDetailsBinding
    lateinit var adapter :MusicAdapter

    companion object{
    var currentPlaylistPos : Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarText)

        currentPlaylistPos = intent.extras?.get("index") as Int

        binding.playlistDeatailsRV.setItemViewCacheSize(13)
        binding.playlistDeatailsRV.setHasFixedSize(true)
        binding.playlistDeatailsRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this,PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,playlistDetails = true)
        binding.playlistDeatailsRV.adapter = adapter
        //testing

        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this,SelectionActivity::class.java))
            //testing
            adapter.refreshPlaylist()
        }

        binding.removeAllBtnPDA.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to Remove All songs ?")
                .setPositiveButton("Yes"){dialog,_->
                    PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog,_->
                    dialog.dismiss()
                }.show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        binding.playlistNamePDA.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.totalSongPDA.text = "Total Songs : ${adapter.itemCount}"
        binding.createOnPDA.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn
        binding.authorNamePDA.text = "~ ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"

        if(adapter.itemCount > 0){
                binding.shuffleBtnPD.visibility = View.VISIBLE
                binding.removeAllBtnPDA.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
//        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
//        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
//        editor.putString("MusicPlaylist",jsonStringPlaylist)
//        editor.apply()
    }

}