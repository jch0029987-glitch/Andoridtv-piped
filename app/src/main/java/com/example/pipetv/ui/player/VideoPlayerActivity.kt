package com.example.pipetv.ui.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pipetv.PipeTVApp

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""
        val app = application as PipeTVApp
        
        // Use the function we just added to the repository
        val streamUrl = app.repository.getVideoStreamUrl(videoId)

        setContent {
            DisposableEffect(Unit) {
                player = ExoPlayer.Builder(this@VideoPlayerActivity).build().apply {
                    setMediaItem(MediaItem.fromUri(streamUrl))
                    prepare()
                    playWhenReady = true
                }
                onDispose {
                    player?.release()
                    player = null
                }
            }

            AndroidView(factory = { context ->
                PlayerView(context).apply {
                    this.player = this@VideoPlayerActivity.player
                }
            })
        }
    }
}
