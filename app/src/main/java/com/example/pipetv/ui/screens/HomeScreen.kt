package com.example.pipetv.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import com.example.pipetv.ui.components.VideoCard
import com.example.pipetv.ui.player.VideoPlayerActivity

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val repo = remember { InvidiousRepository() }
    var videos by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        videos = repo.getTrendingVideos()
        isLoading = false
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Trending", style = MaterialTheme.typography.headlineMedium)
        
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { 
                        // HW Acceleration: Clips the grid rendering to its bounds
                        clip = true 
                    }
            ) {
                items(
                    items = videos,
                    // PERFORMANCE: Stable keys prevent unnecessary recompositions
                    key = { it.videoId ?: it.hashCode() }
                ) { video ->
                    VideoCard(
                        video = video,
                        onClick = {
                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("VIDEO_ID", video.videoId)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
