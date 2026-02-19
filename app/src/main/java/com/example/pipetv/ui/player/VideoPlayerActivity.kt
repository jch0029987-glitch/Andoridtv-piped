package com.example.pipetv.ui.player

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(UnstableApi::class)
class VideoPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null
    private var showQualityDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = intent.getStringExtra("video_url") ?: return

        // PdaNet Optimized Client
        val stealthClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0.0.0")
                    .header("Referer", "http://10.78.240.3:3000/")
                    .build()
                chain.proceed(request)
            }.build()

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(OkHttpDataSource.Factory(stealthClient)))
            .build().apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
                playWhenReady = true
                
                // ERROR HANDLER: If playback fails, tell the user why
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        val reason = if (error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) 
                            "403 Forbidden: Server proxy blocked." else "Error: ${error.errorCodeName}"
                        Toast.makeText(this@VideoPlayerActivity, reason, Toast.LENGTH_LONG).show()
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

                if (showQualityDialog.value) {
                    QualityMenu(
                        onDismiss = { showQualityDialog.value = false },
                        onSelect = { height ->
                            // Force ExoPlayer to switch resolutions
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
                if (showQualityDialog.value) { showQualityDialog.value = false; true } 
                else { finish(); true }
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
    val options = listOf("1080p" to 1080, "720p" to 720, "480p" to 480, "360p" to 360)
    Dialog(onDismissRequest = onDismiss) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
            Column(Modifier.padding(16.dp).width(200.dp)) {
                Text("Select Quality", color = Color.Gray)
                options.forEach { (label, height) ->
                    var isFocused by remember { mutableStateOf(false) }
                    Text(
                        text = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused }
                            .focusable()
                            .clickable { onSelect(height) }
                            .padding(12.dp),
                        color = if (isFocused) Color.Red else Color.White
                    )
                }
            }
        }
    }
}
