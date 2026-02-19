package com.example.pipetv.ui.player

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import okhttp3.OkHttpClient

class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null
    private var showQualityDialog = mutableStateOf(false)

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = intent.getStringExtra("video_url") ?: return

        val stealthClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0")
                .build()
            chain.proceed(request)
        }.build()

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(OkHttpDataSource.Factory(stealthClient)))
            .build().apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
                playWhenReady = true
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

                if (showQualityDialog.value) {
                    QualityMenu(
                        onDismiss = { showQualityDialog.value = false },
                        onSelect = { height ->
                            player?.trackSelectionParameters = player?.trackSelectionParameters
                                ?.buildUpon()
                                ?.setMaxVideoSize(1920, height)
                                ?.build() ?: return@QualityMenu
                            showQualityDialog.value = false
                        }
                    )
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_MENU -> {
                showQualityDialog.value = true
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                player?.let { if (it.isPlaying) it.pause() else it.play() }
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                if (showQualityDialog.value) {
                    showQualityDialog.value = false
                    true
                } else {
                    finish(); true
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}

@Composable
fun QualityMenu(onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
    val options = listOf(
        "1080p" to 1080,
        "720p" to 720,
        "480p" to 480,
        "360p" to 360
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(Modifier.padding(16.dp).width(200.dp)) {
                Text("Quality", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                options.forEach { (label, height) ->
                    var isFocused by remember { mutableStateOf(false) }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused }
                            .focusable()
                            .clickable { onSelect(height) },
                        color = if (isFocused) Color.White.copy(0.1f) else Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(12.dp),
                            color = if (isFocused) Color.Red else Color.White
                        )
                    }
                }
            }
        }
    }
}
