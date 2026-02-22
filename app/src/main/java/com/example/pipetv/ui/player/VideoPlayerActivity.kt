package com.example.pipetv.ui.player

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pipetv.PipeTVApp

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.getStringExtra("VIDEO_ID")
        val app = application as PipeTVApp
        
        if (videoId.isNullOrEmpty()) {
            Log.e("PipeTV", "Playback failed: VideoID is null")
            finish()
            return
        }

        // Invidious usually expects /latest_version?id=VIDEO_ID&itag=18
        val streamUrl = "${app.repository.BASE_URL}/latest_version?id=$videoId&itag=18"
        Log.d("PipeTV", "Attempting to play: $streamUrl")

        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.Builder()
                .setUri(streamUrl)
                .setMimeType(MimeTypes.VIDEO_MP4)
                .build()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        setContent {
            AndroidView(factory = { context ->
                PlayerView(context).apply {
                    this.player = this@VideoPlayerActivity.player
                    this.useController = true
                    this.requestFocus()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
