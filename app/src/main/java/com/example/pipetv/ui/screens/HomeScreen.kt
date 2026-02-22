package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pipetv.PipeTVApp
import com.example.pipetv.ui.components.VideoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, app: PipeTVApp) {
    // Use collectAsState with a clear initial value
    val videos by app.repository.trendingVideos.collectAsState()

    // Keying LaunchedEffect to Unit ensures it ONLY runs once when the screen opens
    LaunchedEffect(Unit) {
        app.repository.fetchTrending()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PipeTV") })
        }
    ) { padding ->
        if (videos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator() // Show this instead of a blank white screen
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(180.dp),
                contentPadding = padding
            ) {
                items(videos.size) { index ->
                    VideoCard(video = videos[index]) {
                        // Playback logic
                    }
                }
            }
        }
    }
}
