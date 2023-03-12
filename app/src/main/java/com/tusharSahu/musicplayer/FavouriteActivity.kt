package com.tusharSahu.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tusharSahu.musicplayer.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding :ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter
    companion object{
        var favouriteSongs : ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backBtnFav.setOnClickListener {
            finish()
        }
        binding.favRV.setHasFixedSize(true)
        binding.favRV.setItemViewCacheSize(13)
        binding.favRV.layoutManager = LinearLayoutManager(this)
        adapter = FavouriteAdapter(this, favouriteSongs)
        binding.favRV.adapter = adapter

        if(favouriteSongs.size < 1){
            binding.shuffleBtnFA.visibility = View.INVISIBLE
        }
        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShffle")
            startActivity(intent)
        }

    }
}