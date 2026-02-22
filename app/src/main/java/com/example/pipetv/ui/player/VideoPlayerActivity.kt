package com.example.pipetv.ui.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pipetv.PipeTVApp

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""
        val app = application as PipeTVApp
        
        // itag 18 is 360p (most compatible), itag 22 is 720p
        val streamUrl = "${app.repository.BASE_URL}/latest_version?id=$videoId&itag=18"

        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        setContent {
            AndroidView(factory = { context ->
                PlayerView(context).apply {
                    this.player = this@VideoPlayerActivity.player
                    this.useController = true // Ensure TV users can see the seek bar
                    this.requestFocus() // Give focus to the player immediately
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
