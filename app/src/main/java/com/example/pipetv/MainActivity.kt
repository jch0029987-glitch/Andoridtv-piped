package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.example.pipetv.data.api.RetrofitClient
import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var videos by remember { mutableStateOf(emptyList<PipedVideo>()) }
    var currentSource by remember { mutableStateOf("PIPED") }
    val scope = rememberCoroutineScope()

    // Load Trending by default
    LaunchedEffect(Unit) {
        videos = RetrofitClient.pipedApi.getTrending("US")
    }

    Column(modifier = Modifier.padding(24.dp)) {
        // --- Search Bar Row ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Videos...") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                scope.launch {
                    if (searchQuery.isNotEmpty()) {
                        videos = RetrofitClient.pipedApi.search(searchQuery)
                    }
                }
            }) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Video Grid ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(videos) { video ->
                VideoCard(video)
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCard(video: PipedVideo) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        onClick = {
            scope.launch {
                try {
                    // Get Stream from Piped
                    val streamData = RetrofitClient.pipedApi.getStream(video.videoId)
                    // Select first stream with audio
                    val url = streamData.videoStreams.firstOrNull { !it.videoOnly }?.url
                    
                    if (url != null) {
                        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", url)
                        }
                        context.startActivity(intent)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16/9f),
                contentScale = ContentScale.Crop
            )
            Text(video.title, maxLines = 2, modifier = Modifier.padding(8.dp))
        }
    }
}
