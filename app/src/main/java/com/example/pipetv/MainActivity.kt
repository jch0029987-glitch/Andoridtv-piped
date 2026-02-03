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
                Toast.makeText(context, "Fetch Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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
                    label = { Text("Search YouTube") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { fetchData() }) { Text("Go") }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { fetchData() }
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

            if (isRefreshing && videos.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                // Ensure ID is trimmed of any accidental whitespace
                val cleanId = video.id.trim()
                
                if (cleanId.isEmpty()) {
                    Toast.makeText(context, "ID Error", Toast.LENGTH_SHORT).show()
                    return@clickable
                }
                
                scope.launch {
                    try {
                        Toast.makeText(context, "Opening Video...", Toast.LENGTH_SHORT).show()
                        val streamData = RetrofitClient.pipedApi.getStream(cleanId)
                        
                        // Pick the first available stream that has audio
                        val url = streamData.videoStreams?.firstOrNull { !it.videoOnly }?.url
                        
                        if (!url.isNullOrEmpty()) {
                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("video_url", url)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "No stream URL from server", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        // This will now show the actual message if a 500 occurs
                        Toast.makeText(context, "Server Error 500: Try a different video", Toast.LENGTH_LONG).show()
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
                text = video.title ?: "Untitled",
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
