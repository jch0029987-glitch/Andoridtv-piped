package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.data.network.AppDownloader
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
    val pipeDownloader = remember { AppDownloader() } // Explicit variable

    var videos by remember { mutableStateOf(emptyList<PipedVideo>()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("Music") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Search function
    val performSearch: (String) -> Unit = { query ->
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Explicit call on pipeDownloader
                val results: List<PipedVideo> = pipeDownloader.search(query)
                videos = results
                isLoading = false
                if (results.isEmpty()) {
                    errorMessage = "No results for '$query'"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error: ${e.localizedMessage}"
                Log.e("PipeTV", "Search failed", e)
            }
        }
    }

    // Initial load
    LaunchedEffect(Unit) { performSearch(searchQuery) }

    Scaffold(
        topBar = {
            Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(title = { Text("PipeTV Home") })
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search Piped/Invidious...") },
                    trailingIcon = {
                        IconButton(onClick = { performSearch(searchQuery) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    singleLine = true
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(errorMessage!!, color = Color.White, modifier = Modifier.padding(16.dp))
                    Button(onClick = { performSearch(searchQuery) }) { Text("Retry") }
                }
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { video ->
                        VideoItem(video)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: PipedVideo) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val videoUrl = "https://piped.kavin.rocks/watch?v=${video.id}"
                val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                    putExtra("video_url", videoUrl)
                }
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            AsyncImage(
                model = video.thumbnail ?: "",
                contentDescription = video.title,
                modifier = Modifier
                    .aspectRatio(16 / 9f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = video.title ?: "Untitled",
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = video.uploader ?: "Unknown",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
