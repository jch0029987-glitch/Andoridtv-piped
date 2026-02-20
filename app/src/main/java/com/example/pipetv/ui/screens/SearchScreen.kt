package com.example.pipetv.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import com.example.pipetv.ui.components.VideoCard
import com.example.pipetv.ui.player.VideoPlayerActivity
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val repo = remember { InvidiousRepository() }
    
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isSearching by remember { mutableStateOf(false) }

    // Debounced Search: Saves data on your hotspot by not searching every keystroke
    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList()
            return@LaunchedEffect
        }
        isSearching = true
        delay(600) // Wait for user to stop typing
        results = repo.searchVideos(query)
        isSearching = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Search Input field optimized for TV Remote
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search YouTube (via Invidious)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )

        if (isSearching) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Searching...", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // GPU Optimization: Rasterizes the grid view for smoother scrolling
                        clip = true
                        compositingStrategy = CompositingStrategy.Auto
                    }
            ) {
                items(results) { video ->
                    VideoCard(
                        video = video,
                        onClick = {
                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("VIDEO_ID", video.videoId)
                            }
                            context.startActivity(intent)
                        },
                        // Individual card GPU layering
                        modifier = Modifier.graphicsLayer {
                            compositingStrategy = CompositingStrategy.ModulateAlpha
                        }
                    )
                }
            }
        }
    }
}
