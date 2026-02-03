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
    var selectedSource by remember { mutableIntStateOf(0) }
    
    val columns = if (LocalConfiguration.current.screenWidthDp > 900) 4 else 2

    val fetchData = {
        scope.launch {
            isRefreshing = true
            try {
                videos = if (selectedSource == 0) {
                    if (searchQuery.isEmpty()) {
                        RetrofitClient.pipedApi.getTrending("US")
                    } else {
                        RetrofitClient.pipedApi.search(searchQuery).items
                    }
                } else {
                    val invidiousResults = if (searchQuery.isEmpty()) {
                        RetrofitClient.invidiousApi.getTrending()
                    } else {
                        RetrofitClient.invidiousApi.search(searchQuery)
                    }
                    
                    // FIXED: Using 'videoId' and 'uploaderName' to match your model
                    invidiousResults.map { inv ->
                        PipedVideo(
                            videoId = inv.videoId,
                            title = inv.title,
                            uploaderName = inv.author,
                            thumbnail = inv.videoThumbnails.firstOrNull()?.url ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Search Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
            isRefreshing = false
        }
    }

    LaunchedEffect(selectedSource) { fetchData() }

    Scaffold(
        topBar = {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Search...") },
                        singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { fetchData() }) { Text("Go") }
                }
                Spacer(Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    SegmentedButton(selected = selectedSource == 0, onClick = { selectedSource = 0 },
                        shape = SegmentedButtonDefaults.itemShape(0, 2), label = { Text("Piped") })
                    SegmentedButton(selected = selectedSource == 1, onClick = { selectedSource = 1 },
                        shape = SegmentedButtonDefaults.itemShape(1, 2), label = { Text("Invidious") })
                }
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { fetchData() },
            modifier = Modifier.padding(padding).fillMaxSize()
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
                scope.launch {
                    try {
                        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                        // FIXED: Using video.videoId instead of video.id
                        val streamData = RetrofitClient.pipedApi.getStream(video.videoId)
                        val url = streamData.videoStreams.firstOrNull { !it.videoOnly }?.url
                        
                        if (!url.isNullOrEmpty()) {
                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("video_url", url)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Stream not found", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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
                text = video.title,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
