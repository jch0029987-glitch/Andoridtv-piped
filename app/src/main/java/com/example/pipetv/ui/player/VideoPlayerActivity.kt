package com.example.pipetv.ui.player

import android.os.Bundle
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
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.pipetv.PipeTVApp
import com.example.pipetv.network.InvidiousRepository

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""
        val app = application as PipeTVApp

        setContent {
            val repo = remember { InvidiousRepository(app) }
            var streamUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(videoId) {
                streamUrl = repo.getVideoStreamUrl(videoId)
            }

            Box(Modifier.fillMaxSize().background(Color.Black)) {
                streamUrl?.let { url ->
                    AndroidView(
                        factory = { ctx ->
                            // Use the PDAnet-masked client for the video stream
                            val dataSourceFactory = OkHttpDataSource.Factory(app.okHttpClient)
                            val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
                            
                            val exo = ExoPlayer.Builder(ctx)
                                .setMediaSourceFactory(mediaSourceFactory)
                                .build().apply {
                                    setMediaItem(MediaItem.fromUri(url))
                                    prepare()
                                    playWhenReady = true
                                }
                            player = exo
                            PlayerView(ctx).apply { this.player = exo }
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
