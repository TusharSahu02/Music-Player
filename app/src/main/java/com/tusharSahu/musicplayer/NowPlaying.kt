package com.tusharSahu.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tusharSahu.musicplayer.databinding.FragmentNowPlayingBinding

class NowPlaying : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding :FragmentNowPlayingBinding
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)

        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playPauseBtnNP.setOnClickListener{
            if(PlayerActivity.isPlaying){
                pauseMusic()
            }else{
                playMusic()
            }
        }

        binding.nextBtnNP.setOnClickListener {
            setSongPosition(increment = true )
            PlayerActivity.musicSerive!!.createMediaPlayer()
            PlayerActivity.isPlaying = true
            PlayerActivity.binding.songNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            binding.albumNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].album
            PlayerActivity.musicSerive!!.showNotification(R.drawable.pause_24)
            playMusic()
        }

        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext() , intent, null)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicSerive != null){
            binding.root.visibility = View.VISIBLE
            binding.songNameNP.isSelected = true
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            binding.albumNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].album
            if(PlayerActivity.isPlaying){
                binding.playPauseBtnNP.setIconResource(R.drawable.pause)
            }else{
                binding.playPauseBtnNP.setIconResource(R.drawable.play)
            }
        }

    }

    private fun playMusic(){
        PlayerActivity.musicSerive!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause)
        PlayerActivity.musicSerive!!.showNotification(R.drawable.pause_24)
        PlayerActivity.binding.nextSongBtn.setImageResource( R.drawable.pause)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic(){
        PlayerActivity.musicSerive!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play)
        PlayerActivity.musicSerive!!.showNotification(R.drawable.play_24)
        PlayerActivity.binding.nextSongBtn.setImageResource( R.drawable.play)
        PlayerActivity.isPlaying = false
    }

}