package com.example.pipetv.ui.screens

import android.content.Intent
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
    val videos by app.repository.trendingVideos.collectAsState()

    LaunchedEffect(Unit) {
        app.repository.fetchTrending()
    }

    Scaffold { padding ->
        if (videos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(200.dp),
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(videos) { video ->
                    VideoCard(video = video) {
                        val intent = Intent(navController.context, VideoPlayerActivity::class.java).apply {
                            putExtra("VIDEO_ID", video.videoId)
                        }
                        navController.context.startActivity(intent)
                    }
                }
            }
        }
    }
}
