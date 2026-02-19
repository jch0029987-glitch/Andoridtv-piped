package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Updated to use the Invidious Repository
    private val repository = InvidiousRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                MainScreen(repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(repository: InvidiousRepository) {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("Music") }
    val context = LocalContext.current

    val performSearch: (String) -> Unit = { query ->
        scope.launch {
            isLoading = true
            videos = repository.searchVideos(query)
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { performSearch(searchQuery) }

    Scaffold(
        topBar = {
            Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(title = { Text("InvidiousTV") })
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search (Stealth Mode)...") },
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
        Box(Modifier.padding(padding).fillMaxSize().background(Color.Black)) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { video ->
                        VideoItem(video, repository)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: InvidiousVideo, repository: InvidiousRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                scope.launch {
                    val streamUrl = repository.getStreamUrl(video.videoId)
                    if (streamUrl != null) {
                        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", streamUrl)
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Stream not available", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(16 / 9f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = video.title,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = video.author,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
