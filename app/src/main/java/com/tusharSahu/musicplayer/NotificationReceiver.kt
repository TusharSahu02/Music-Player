package com.tusharSahu.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> {
                preNextSong(increment = false)
            }
            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying)
                    pauseMusic()
                else
                    playMusic()
            }
            ApplicationClass.NEXT -> {
                preNextSong(increment = true)
            }
            ApplicationClass.EXIT -> {
                PlayerActivity.musicSerive!!.stopForeground(true)
                PlayerActivity.musicSerive!!.mediaPlayer!!.release()
                PlayerActivity.musicSerive = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicSerive!!.mediaPlayer!!.start()
        PlayerActivity.musicSerive!!.showNotification(R.drawable.pause_24)
        PlayerActivity.binding.playPause.setImageResource(R.drawable.pause_24)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicSerive!!.mediaPlayer!!.pause()
        PlayerActivity.musicSerive!!.showNotification(R.drawable.play_24)
        PlayerActivity.binding.playPause.setImageResource(R.drawable.play_24)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play)
    }

    private fun preNextSong(increment: Boolean) {
        setSongPosition(increment = increment)
        PlayerActivity.musicSerive!!.createMediaPlayer()
        PlayerActivity.isPlaying = true
        PlayerActivity.binding.songNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        NowPlaying.binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        NowPlaying.binding.albumNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].album
        playMusic()
        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFav){
            PlayerActivity.binding.favBtnPA.setImageResource(R.drawable.lover)
        }else{
            PlayerActivity.binding.favBtnPA.setImageResource(R.drawable.heart)
        }
    }
}