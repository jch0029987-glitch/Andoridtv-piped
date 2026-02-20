package com.example.pipetv.ui.player

import android.os.Bundle
import android.view.SurfaceView
import android.widget.Toast
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
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.pipetv.network.InvidiousRepository

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val repo = remember { InvidiousRepository() }
            var streamUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(videoId) {
                streamUrl = repo.getVideoStreamUrl(videoId)
                if (streamUrl == null) {
                    Toast.makeText(context, "Link expired or invalid", Toast.LENGTH_SHORT).show()
                }
            }

            Box(Modifier.fillMaxSize().background(Color.Black)) {
                if (streamUrl != null) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                // Defaulting to SurfaceView for GPU performance
                                // We removed the useTextureView line to prevent build errors
                                
                                // GPU Overlay: Keeps UI snappy while video plays
                                (videoSurfaceView as? SurfaceView)?.setZOrderMediaOverlay(true)

                                // Fix for ERROR_CODE_FAILED_RUNTIME_CHECK
                                val renderersFactory = DefaultRenderersFactory(ctx)
                                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)

                                player = ExoPlayer.Builder(ctx, renderersFactory).build().also { exo ->
                                    val mediaItem = MediaItem.fromUri(streamUrl!!)
                                    exo.setMediaItem(mediaItem)
                                    exo.prepare()
                                    
                                    exo.addListener(object : Player.Listener {
                                        override fun onPlayerError(error: PlaybackException) {
                                            Toast.makeText(ctx, "Exo Error: ${error.errorCodeName}", Toast.LENGTH_LONG).show()
                                        }
                                    })
                                    
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

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
