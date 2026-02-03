package com.example.pipetv.ui.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.pipetv.R

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoUrl = intent.getStringExtra("video_url") ?: return
        val playerView = findViewById<PlayerView>(R.id.player_view)

        // 1. Create a DataSource factory that mimics a browser
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)

        // 2. Build the player using that factory
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(httpDataSourceFactory))
            .build()
            .also {
                playerView.player = it
                val mediaItem = MediaItem.fromUri(videoUrl)
                it.setMediaItem(mediaItem)
                it.prepare()
                it.playWhenReady = true
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
