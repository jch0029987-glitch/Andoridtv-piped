package com.example.pipetv.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val repository = remember { InvidiousRepository() }
    val scope = rememberCoroutineScope()
    var trendingVideos by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch trending videos immediately on launch
    LaunchedEffect(Unit) {
        trendingVideos = repository.getTrendingVideos()
        isLoading = false
    }

    Box(Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
        } else {
            Column {
                Text(
                    "Trending Now",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4), // 4 videos across on a TV screen
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(trendingVideos) { video ->
                        VideoCard(video = video) {
                            scope.launch {
                                val url = repository.getStreamUrl(video.videoId)
                                if (url != null) {
                                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                        putExtra("video_url", url)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCard(video: InvidiousVideo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = video.title,
                modifier = Modifier.padding(8.dp),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
