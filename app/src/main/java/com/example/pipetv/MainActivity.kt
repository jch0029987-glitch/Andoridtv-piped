package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.pipetv.data.network.AppDownloader
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamInfo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            NewPipe.init(AppDownloader())
        } catch (e: Exception) {
            Log.e("PipeTV", "Init Error", e)
        }
        setContent { PipeTVTheme { MainScreen() } }
    }
}

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(emptyList<StreamInfoItem>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                videos = withContext(Dispatchers.IO) {
                    val service = ServiceList.YouTube
                    // v0.24.4 uses getKiosk
                    val kiosk = service.kioskList.getKiosk("Trending")
                    kiosk.fetchPage()
                    kiosk.itemsPage.items as List<StreamInfoItem>
                }
            } catch (e: Exception) {
                Log.e("PipeTV", "Fetch Error", e)
            }
            isLoading = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                items(videos) { video ->
                    VideoItem(video)
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: StreamInfoItem) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(Modifier.padding(8.dp).clickable {
        scope.launch(Dispatchers.IO) {
            try {
                // v0.24.4 static call for StreamInfo
                val info = StreamInfo.getInfo(ServiceList.YouTube, video.getUrl())
                val streamUrl = info.videoStreams.firstOrNull()?.getUrl() ?: info.hlsUrl
                
                withContext(Dispatchers.Main) {
                    if (streamUrl != null) {
                        context.startActivity(Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", streamUrl)
                        })
                    } else {
                        Toast.makeText(context, "No stream found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PipeTV", "Click Error", e)
            }
        }
    }) {
        Column {
            AsyncImage(
                model = video.thumbnails.firstOrNull()?.url,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16/9f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = video.getName() ?: "Unknown",
                modifier = Modifier.padding(8.dp),
                maxLines = 2
            )
        }
    }
}
