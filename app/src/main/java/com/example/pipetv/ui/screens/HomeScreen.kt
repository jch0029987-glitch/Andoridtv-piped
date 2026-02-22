package com.example.pipetv.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pipetv.PipeTVApp
import com.example.pipetv.ui.components.VideoCard
import com.example.pipetv.ui.player.VideoPlayerActivity

@Composable
fun HomeScreen(navController: NavController, app: PipeTVApp) {
    // Collect the trending videos from the repository
    val videos by app.repository.trendingVideos.collectAsState()

    // Trigger the fetch when the screen opens
    LaunchedEffect(Unit) {
        app.repository.fetchTrending()
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Text(text = "PipeTV - Trending", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        if (videos.isEmpty()) {
            // Show loading state while waiting for Termux server
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Adaptive grid: Fixed columns work better for TV D-pad focus flow
            LazyVerticalGrid(
                columns = GridCells.Fixed(4), 
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(videos) { video ->
                    VideoCard(video = video) {
                        Log.d("PipeTV", "Launching Player for ID: ${video.videoId}")
                        
                        val intent = Intent(navController.context, VideoPlayerActivity::class.java).apply {
                            putExtra("VIDEO_ID", video.videoId)
                            // Flags to ensure smooth transition on TV
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        navController.context.startActivity(intent)
                    }
                }
            }
        }
    }
}
