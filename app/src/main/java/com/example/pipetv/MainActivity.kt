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
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pipetv.data.api.RetrofitClient
import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PipeTVTheme { MainScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var videos by remember { mutableStateOf(emptyList<PipedVideo>()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val columns = if (LocalConfiguration.current.screenWidthDp > 900) 4 else 2

    val fetchData = {
        scope.launch {
            isRefreshing = true
            try {
                videos = if (searchQuery.isEmpty()) {
                    RetrofitClient.pipedApi.getTrending("US")
                } else {
                    RetrofitClient.pipedApi.search(searchQuery).items ?: emptyList()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "API Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) { fetchData() }

    Scaffold(
        topBar = {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Search") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { fetchData() }) { Text("Go") }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = { fetchData() }) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(videos) { VideoCard(it) }
                }
            }
            if (isRefreshing && videos.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun VideoCard(video: PipedVideo) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            val videoId = video.id
            if (videoId.isEmpty()) return@clickable
            
            scope.launch {
                try {
                    Toast.makeText(context, "Fetching Stream...", Toast.LENGTH_SHORT).show()
                    val response = RetrofitClient.pipedApi.getStream(videoId)
                    
                    // Priority: HLS -> Combined Stream -> Video Only
                    val streamUrl = response.hls ?: 
                                    response.videoStreams?.firstOrNull { !it.videoOnly }?.url ?:
                                    response.videoStreams?.firstOrNull()?.url

                    if (!streamUrl.isNullOrEmpty()) {
                        context.startActivity(Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", streamUrl)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    } else {
                        Toast.makeText(context, "No stream found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Logic to help debug 500
                    val errorMsg = e.message ?: ""
                    if (errorMsg.contains("500")) {
                        Toast.makeText(context, "Server 500: Instance is failing to proxy this video", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    ) {
        Column {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16f/9f),
                contentScale = ContentScale.Crop
            )
            Text(video.title ?: "", Modifier.padding(8.dp), maxLines = 2)
        }
    }
}
