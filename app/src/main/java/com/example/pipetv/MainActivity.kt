package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
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
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNewPipe("US")
        setContent { PipeTVTheme { MainScreen { initNewPipe(it) } } }
    }

    private fun initNewPipe(countryCode: String) {
        try {
            NewPipe.init(
                AppDownloader(),
                Localization.fromLocale(Locale.ENGLISH),
                ContentCountry(countryCode)
            )
        } catch (e: Exception) {
            Log.e("PipeTV", "Init Failed", e)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onRegionChange: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(emptyList<StreamInfoItem>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fetchVideos = {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                val service = ServiceList.YouTube
                // Corrected for v0.24.4: use getSearchExtractor
                val search = service.getSearchExtractor("trending music")
                search.fetchPage()
                val items = search.initialPage.items.filterIsInstance<StreamInfoItem>()
                
                withContext(Dispatchers.Main) {
                    videos = items
                    isLoading = false
                    if (items.isEmpty()) errorMessage = "No content found."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Network Error: Please try again."
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) { fetchVideos() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PipeTV Home") }, actions = {
                IconButton(onClick = { fetchVideos() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            })
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().background(Color.Black)) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMessage!!, color = Color.White)
                    Button(onClick = { fetchVideos() }, Modifier.padding(top = 16.dp)) { Text("Retry") }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
                    items(videos) { video -> VideoItem(video) }
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: StreamInfoItem) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.padding(8.dp).clickable {
            scope.launch(Dispatchers.IO) {
                try {
                    val info = StreamInfo.getInfo(ServiceList.YouTube, video.url)
                    val streamUrl = info.videoStreams.firstOrNull()?.url ?: info.hlsUrl
                    withContext(Dispatchers.Main) {
                        if (streamUrl != null) {
                            context.startActivity(Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("video_url", streamUrl)
                            })
                        }
                    }
                } catch (e: Exception) { Log.e("PipeTV", "Play Error", e) }
            }
        },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            AsyncImage(
                model = video.thumbnails?.firstOrNull()?.url,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16/9f).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(8.dp)) {
                Text(video.name ?: "", color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(video.uploaderName ?: "", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
