package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // REQUIRED FOR 'by'
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pipetv.PipeTVApp
import com.example.pipetv.ui.components.VideoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, app: PipeTVApp) {
    // Accessing the repository defined in PipeTVApp
    val videos by app.repository.trendingVideos.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        app.repository.fetchTrending()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PipeTV") }, actions = {
                IconButton(onClick = { navController.navigate("search") }) { Text("🔍") }
            })
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(videos) { video ->
                VideoCard(video = video) {
                    // Playback navigation logic
                }
            }
        }
    }
}
