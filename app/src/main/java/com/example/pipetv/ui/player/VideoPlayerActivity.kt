package com.example.pipetv.ui.player

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import okhttp3.OkHttpClient

@OptIn(UnstableApi::class)
class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = intent.getStringExtra("video_url")

        // 1. Validate URL before passing to ExoPlayer to prevent Malformed URL crash
        if (videoUrl.isNullOrBlank() || !videoUrl.startsWith("http")) {
            Toast.makeText(this, "Error: Invalid Stream URL\n$videoUrl", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val stealthClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/122.0.0.0")
                    .header("Referer", "http://10.78.240.3:3000/")
                    .build()
                chain.proceed(request)
            }.build()

        // 2. Android 14 strict codec handling
        val renderersFactory = DefaultRenderersFactory(this)
            .setEnableDecoderFallback(true)

        player = ExoPlayer.Builder(this, renderersFactory)
            .setMediaSourceFactory(DefaultMediaSourceFactory(OkHttpDataSource.Factory(stealthClient)))
            .build().apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
                playWhenReady = true
                
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        val msg = "Playback Error: ${error.errorCodeName}\n${error.cause?.message}"
                        Toast.makeText(this@VideoPlayerActivity, msg, Toast.LENGTH_LONG).show()
                    }
                })
            }

        setContent {
            Box(Modifier.fillMaxSize().background(Color.Black)) {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            this.player = this@VideoPlayerActivity.player
                            this.useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> { finish(); true }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                player?.let { if (it.isPlaying) it.pause() else it.play() }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
