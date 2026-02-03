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
        setContent {
            PipeTVTheme {
                MainScreen()
            }
        }
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
    
    // Column count based on screen width (for TV vs Mobile)
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
                Toast.makeText(context, "Search Failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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
                    label = { Text("Search...") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { fetchData() }) { Text("Search") }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { fetchData() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(videos) { video ->
                        VideoCard(video)
                    }
                }
            }

            // Central Loading Spinner
            if (isRefreshing && videos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun VideoCard(video: PipedVideo) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Ensure we have a clean 11-char ID
                val cleanId = video.id.trim()
                
                if (cleanId.isBlank()) {
                    Toast.makeText(context, "ID Error", Toast.LENGTH_SHORT).show()
                    return@clickable
                }
                
                scope.launch {
                    try {
                        Toast.makeText(context, "Resolving Stream...", Toast.LENGTH_SHORT).show()
                        val streamData = RetrofitClient.pipedApi.getStream(cleanId)
                        
                        /** * LIBRETUBE LOGIC:
                         * 1. Priority: HLS (.m3u8) - Most stable for Android TV/ExoPlayer
                         * 2. Fallback: First Video+Audio stream (Combined)
                         */
                        val videoUrl = streamData.hls ?: 
                                      streamData.videoStreams?.firstOrNull { !it.videoOnly }?.url
                        
                        if (!videoUrl.isNullOrEmpty()) {
                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("video_url", videoUrl)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "No stream found for this video", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        // Catch Error 500 here
                        val msg = if (e.localizedMessage?.contains("500") == true) 
                            "Error 500: Server is temporarily blocking requests" 
                            else "Player Error: ${e.localizedMessage}"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
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
            Text(
                text = video.title ?: "No Title",
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
