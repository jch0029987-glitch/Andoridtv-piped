package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pipetv.data.api.RetrofitClient
import com.example.pipetv.data.api.InvidiousVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PipeTVTheme { MainScreen() } }
    }
}

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var videos by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            videos = RetrofitClient.invidiousApi.getTrending()
        } catch (e: Exception) {
            Toast.makeText(context, "API Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
        isLoading = false
    }

    Scaffold { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(videos) { video ->
                    VideoItem(video)
                }
            }
            if (isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun VideoItem(video: InvidiousVideo) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(Modifier.clickable {
        scope.launch {
            try {
                Toast.makeText(context, "Opening Stream...", Toast.LENGTH_SHORT).show()
                val data = RetrofitClient.invidiousApi.getVideoData(video.videoId)
                
                var streamUrl = data.formatStreams?.firstOrNull()?.url
                
                if (!streamUrl.isNullOrEmpty()) {
                    // Prepend domain if relative
                    if (streamUrl.startsWith("/")) {
                        streamUrl = "https://yewtu.be$streamUrl"
                    }
                    // Force local proxying to avoid 403 IP lock
                    if (!streamUrl.contains("local=true")) {
                        streamUrl += if (streamUrl.contains("?")) "&local=true" else "?local=true"
                    }

                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                        putExtra("video_url", streamUrl)
                    }
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Stream Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Column {
            AsyncImage(
                model = video.videoThumbnails?.firstOrNull()?.url,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16/9f),
                contentScale = ContentScale.Crop
            )
            Text(video.title, Modifier.padding(8.dp), maxLines = 2, style = MaterialTheme.typography.bodySmall)
        }
    }
}
