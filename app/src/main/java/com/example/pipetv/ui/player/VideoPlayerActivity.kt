package com.example.pipetv.ui.player

import android.os.Bundle
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.pipetv.network.InvidiousRepository

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""

        setContent {
            val repo = remember { InvidiousRepository() }
            var streamUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(videoId) {
                streamUrl = repo.getVideoStreamUrl(videoId)
            }

            Box(Modifier.fillMaxSize().background(Color.Black)) {
                if (streamUrl != null) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                // Use SurfaceView for best hardware sync on CCwGTV
                                setUseTextureView(false)
                                
                                // GPU Trick: Move video to the hardware overlay plane
                                (videoSurfaceView as? SurfaceView)?.setZOrderMediaOverlay(true)

                                player = ExoPlayer.Builder(ctx).build().also { exo ->
                                    exo.setMediaItem(MediaItem.fromUri(streamUrl!!))
                                    exo.prepare()
                                    exo.playWhenReady = true
                                }
                                this.player = player
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
