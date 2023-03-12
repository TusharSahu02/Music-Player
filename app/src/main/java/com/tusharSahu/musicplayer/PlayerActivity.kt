package com.tusharSahu.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tusharSahu.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {


    companion object {
        lateinit var musicListPA: ArrayList<Music>
        var songPosition: Int = 0;
        var isPlaying: Boolean = false

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding

        var musicSerive: MusicService? = null
//        var mediaPlayer: MediaPlayer? = null

        var repeat: Boolean = false
        var nowPlayingId: String = ""

        var isFav: Boolean = false
        var fIndex: Int = -1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()

        binding.backBtnPA.setOnClickListener {
            finish()
        }

        binding.equalizerBtnPA.setOnClickListener {

            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicSerive!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 101)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Equalizer feature not supported in your device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File !!"))
        }

        binding.favBtnPA.setOnClickListener {
            if (isFav) {
                isFav = false
                binding.favBtnPA.setImageResource(R.drawable.heart)
                FavouriteActivity.favouriteSongs.removeAt(fIndex)
            } else {
                isFav = true
                binding.favBtnPA.setImageResource(R.drawable.lover)
                FavouriteActivity.favouriteSongs.add(musicListPA[songPosition])
            }
        }

        binding.playPause.setOnClickListener {
            if (isPlaying) {
                pausemusic()
            } else {
                playmusic()
            }
        }

        binding.prevSongBtn.setOnClickListener {
            prevNextsong(increment = false)
        }
        binding.nextSongBtn.setOnClickListener {
            prevNextsong(increment = true)
        }

        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicSerive!!.mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        binding.repeatBtnPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                Toast.makeText(this, "Looped On", Toast.LENGTH_SHORT).show()
            } else {
                repeat = false
                Toast.makeText(this, "Looped Off", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setLayout() {
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        binding.songNamePA.text = musicListPA[songPosition].title

        if (isFav){
            binding.favBtnPA.setImageResource(R.drawable.lover)
        }else{
            binding.favBtnPA.setImageResource(R.drawable.heart)
        }
    }


    private fun playmusic() {
        binding.playPause.setImageResource(R.drawable.pause)
        musicSerive!!.showNotification(R.drawable.pause_24)
        isPlaying = true
        musicSerive!!.mediaPlayer!!.start()
    }

    private fun pausemusic() {
        binding.playPause.setImageResource(R.drawable.play)
        musicSerive!!.showNotification(R.drawable.play_24)
        isPlaying = false
        musicSerive!!.mediaPlayer!!.pause()
    }

    private fun prevNextsong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicSerive = binder.currentService()
        createMediaPlayer()
        musicSerive!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicSerive = null
    }

    private fun initLayout() {
        val music_PA_icon: ImageView = findViewById(R.id.music_img_PA)
        Glide.with(this).load(R.drawable.music).into(music_PA_icon)

        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()

            }

            "MainActivity" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
            }
            "MusicAdapterSearch" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "NowPlaying" -> {
                setLayout()
                binding.startSeekBarTV.text =
                    formatDuration(musicSerive!!.mediaPlayer!!.currentPosition.toLong())
                binding.endSeekBarTV.text =
                    formatDuration(musicSerive!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicSerive!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicSerive!!.mediaPlayer!!.duration
                if (isPlaying) binding.playPause.setImageResource(R.drawable.pause)
                else binding.playPause.setImageResource(R.drawable.play)
            }
            "FavouriteApdater" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                setLayout()
            }
            "FavouriteShffle" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                musicListPA.shuffle()
                setLayout()
            }
            "PlaylistDetailsAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
            }
            "PlaylistDetailsShuffle" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun createMediaPlayer() {
        try {
            if (musicSerive!!.mediaPlayer == null) musicSerive!!.mediaPlayer = MediaPlayer()

            musicSerive!!.mediaPlayer!!.reset()
            musicSerive!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicSerive!!.mediaPlayer!!.prepare()
            musicSerive!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPause.setImageResource(R.drawable.pause)
            musicSerive!!.showNotification(R.drawable.pause_24)
            binding.startSeekBarTV.text =
                formatDuration(musicSerive!!.mediaPlayer!!.currentPosition.toLong())
            binding.endSeekBarTV.text =
                formatDuration(musicSerive!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicSerive!!.mediaPlayer!!.duration
            musicSerive!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id


        } catch (e: Exception) {
            return
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 || resultCode == RESULT_OK) {
            return
        }
    }

}