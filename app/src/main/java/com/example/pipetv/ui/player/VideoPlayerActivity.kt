package com.example.pipetv.ui.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.pipetv.network.InvidiousRepository
import kotlinx.coroutines.launch

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Get the Video ID from the Intent
        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val scope = rememberCoroutineScope()
            val repo = remember { InvidiousRepository() }
            
            // 2. Track the stream URL state
            var streamUrl by remember { mutableStateOf<String?>(null) }

            // 3. Fetch the actual stream link when the activity opens
            LaunchedEffect(videoId) {
                if (videoId.isNotEmpty()) {
                    // We call your repo to get the playback URL
                    streamUrl = repo.getVideoStreamUrl(videoId)
                }
            }

            // 4. Show the player once we have a URL
            if (streamUrl != null) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = ExoPlayer.Builder(ctx).build().also { exo ->
                                val mediaItem = MediaItem.fromUri(streamUrl!!)
                                exo.setMediaItem(mediaItem)
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

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
