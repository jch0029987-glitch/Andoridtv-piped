package com.example.pipetv.ui.player

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoPlayerActivity : ComponentActivity() {

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep the screen on while playing video
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Get the stream URL passed from MainActivity
        val videoUrl = intent.getStringExtra("video_url") ?: ""

        setContent {
            val context = LocalContext.current
            
            // Initialize ExoPlayer and remember its state across recompositions
            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true // Auto-play once ready
                }
            }

            // A dark container for the player
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Interop: Wrapping the classic Media3 PlayerView for TV controls
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            
                            // TV-Specific UI Settings
                            useController = true
                            setShowNextButton(false)
                            setShowPreviousButton(false)
                            
                            // Ensure the player takes focus so the D-Pad works
                            requestFocus()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // CRITICAL: Cleanup the player when the user leaves this screen
            DisposableEffect(Unit) {
                onDispose {
                    exoPlayer.stop()
                    exoPlayer.release()
                }
            }
        }
    }

    // Pause playback if the user hits the Home button
    override fun onPause() {
        super.onPause()
        // Simple global reference check if you need to pause manually
    }
}
