package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize NewPipe Extractor
        NewPipe.init(AppDownloader())
        setContent { PipeTVTheme { MainScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var videos by remember { mutableStateOf(emptyList<StreamInfoItem>()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var region by remember { mutableStateOf("US") } // Custom Setting

    // Fetching logic using NewPipe Extractor
    val fetchTrending = {
        scope.launch {
            isLoading = true
            try {
                videos = withContext(Dispatchers.IO) {
                    val service = ServiceList.YouTube
                    val kiosk = service.kioskList.getById("Trending")
                    kiosk.fetchPage()
                    kiosk.itemsPage.items as List<StreamInfoItem>
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isLoading = false
        }
    }

    LaunchedEffect(region) { fetchTrending() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PipeTV - $region Trending") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(videos) { video ->
                    VideoItem(video)
                }
            }
            if (isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Content Settings") },
                text = {
                    Column {
                        Text("Select Region")
                        Row {
                            Button(onClick = { region = "US"; showSettings = false }) { Text("US") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { region = "GB"; showSettings = false }) { Text("UK") }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showSettings = false }) { Text("Close") } }
            )
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
                // Get direct stream URLs using the Extractor
                val info = ServiceList.YouTube.getStreamExtractor(video.url).fetchAndGetInfo()
                val videoUrl = info.videoStreams.firstOrNull()?.url ?: info.hlsUrl
                
                withContext(Dispatchers.Main) {
                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                        putExtra("video_url", videoUrl)
                    }
                    context.startActivity(intent)
                }
            } catch (e: Exception) { /* Handle Error */ }
        }
    }) {
        Column {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16/9f),
                contentScale = ContentScale.Crop
            )
            Text(video.name, Modifier.padding(8.dp), maxLines = 2)
        }
    }
}
